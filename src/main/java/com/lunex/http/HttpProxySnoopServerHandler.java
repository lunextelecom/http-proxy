package com.lunex.http;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.lunex.logging.LoggingProcessor;
import com.lunex.logging.Statsd;
import com.lunex.rule.MetricRulePattern;
import com.lunex.rule.RoutingRulePattern;
import com.lunex.util.Configuration;
import com.lunex.util.Constants;
import com.lunex.util.Constants.EVerb;
import com.lunex.util.HostAndPort;
import com.lunex.util.LogObject;
import com.lunex.util.ParameterHandler;

/**
 * Server handler for netty server
 * 
 * @author BaoLe
 * @update DuyNguyen
 */
public class HttpProxySnoopServerHandler extends SimpleChannelInboundHandler<HttpObject> {

  static final Logger logger = LoggerFactory.getLogger(HttpProxySnoopServerHandler.class);
  private LastHttpContent trailer;
  private RoutingRulePattern routingRulePattern;
  private MetricRulePattern metricRulePattern;
  private Statsd statsd;
  private String statusResponse;

  private HttpRequest request;
  private Boolean isException = false;
  private Exception exception;
  private HttpContent requestContent;
  private DefaultHttpResponse defaultHttpResponse;
  private final StringBuilder responseContentBuilder = new StringBuilder();

  private LogObject logObject;

  public HttpProxySnoopServerHandler() {}

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    // write response
    if (isException) {
      exceptionCaught(ctx, exception);
      return;
    }
    if (!writeResponse(trailer, ctx)) {
      // If keep-alive is off, close the connection once the content is fully written.
      ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    } else {
      ctx.flush();
    }

    this.processLogging();
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
    if (msg instanceof HttpRequest) {
      this.request = (HttpRequest) msg;
      Channel channel = ctx.channel();
      SocketAddress address = channel.remoteAddress();
      
      //basic authenticate
      boolean isOK = authenticateFilter();
      if(!isOK){
        return;
      }
      // Ip filter
      isOK = ipFilter(address.toString());
      if(!isOK){
        return;
      }

      routingRulePattern = Configuration.getRoutingRule().selectRulePattern(this.request.getUri());
      responseContentBuilder.setLength(0);
      logObject = new LogObject();
      logObject.setRequest(this.request.getUri());
      logObject.setRequestHeaders(((HttpRequest) msg).headers().entries().toString());
      logObject.setMethod(EVerb.valueOf(this.request.getMethod().toString()));
      logObject.setClient(address.toString());
    }

    if (msg instanceof HttpContent) {
      if (!isException) {
        HttpContent httpContent = (HttpContent) msg;
        if (httpContent.content() != null && httpContent.content().isReadable()) {
          String dataContent = httpContent.content().toString(CharsetUtil.UTF_8);
          logObject.setRequestContent(dataContent);
        }
        requestContent = httpContent;

        if (msg instanceof LastHttpContent) {
          trailer = (LastHttpContent) msg;
          // call target to request
          this.processCallTarget();
        }
      }
    }
  }

  /**
   * Submit request to target from Balancer
   * 
   * @author BaoLe
   */
  private void processCallTarget() {
    if (request == null) {
      return;
    }
    if (routingRulePattern == null) {
      return;
    }
    final CountDownLatch responseWaiter = new CountDownLatch(1);
    HostAndPort target = routingRulePattern.getBalancingStrategy().selectTarget();
    if (target == null) {
      logger.error("target is null");
      responseContentBuilder.append("Target is null");
      return;
    }
    String targetUrl = target.getHost() + ":" + target.getPort();
    logObject.setTarget(targetUrl);

    HttpProxySnoopClient client = new HttpProxySnoopClient(targetUrl, new CallbackHTTPVisitor() {
      @Override
      public void doJob(ChannelHandlerContext ctx, HttpObject msg) {
        super.doJob(ctx, msg);
        responseWaiter.countDown();
        if (msg instanceof DefaultHttpResponse) {
          defaultHttpResponse = (DefaultHttpResponse) msg;
          HttpResponseStatus st = defaultHttpResponse.getStatus();
          Integer code = st.code();
          statusResponse = code.toString();
          return;
        }
        if (msg instanceof HttpContent) {
          HttpContent httpContent = (HttpContent) msg;
          ByteBuf content = httpContent.content();
          if (content.isReadable()) {
            String dataContent = content.toString(CharsetUtil.UTF_8);
            responseContentBuilder.append(dataContent);
            logObject.setResponseContent(dataContent);
          }
        }
      }
    });
    try {
      // start write metric
      metricRulePattern = Configuration.getMetricRule().selectRulePattern(this.request);
      if (metricRulePattern != null) {
        String metric =
            metricRulePattern.getMetric().replace("target.",
                targetUrl.replace(".", "_").replace(":", "_") + ".");
        statsd = Statsd.start(metric, ParameterHandler.METRIC_HOST, ParameterHandler.METRIC_PORT);
      }
      client.submitRequest(request, requestContent);
    } catch (Exception e) {
      responseWaiter.countDown();
      isException = true;
      exception = new BadRequestException(e);
      logger.error(e.getMessage());
    }
    try {
      responseWaiter.await(3, TimeUnit.SECONDS);
      client.shutdown();
      logger.info("Shutdown client");
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * Write response from http client to client of netty server
   * 
   * @author BaoLe
   * @param currentObj
   * @param ctx
   * @return
   */
  private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
    logger.info("Write response");
    // Decide whether to close the connection or not.
    boolean keepAlive = isKeepAlive(request);

    // Build the response object.
    FullHttpResponse response =
        new DefaultFullHttpResponse(defaultHttpResponse.getProtocolVersion(),
            defaultHttpResponse.getStatus(), Unpooled.copiedBuffer(
                responseContentBuilder.toString(), CharsetUtil.UTF_8));

    response.headers().set(CONTENT_TYPE, defaultHttpResponse.headers().get("Content-Type"));

    if (keepAlive) {
      // Add 'Content-Length' header only for a keep-alive connection.
      response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
      // Add keep alive header as per:
      // http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
      response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
    }

    // Write the response.
    ctx.writeAndFlush(response);

    return keepAlive;
  }

  /**
   * Process logging(logging + metric)
   * 
   * @author BaoLe
   * @update DuyNguyen
   */
  private void processLogging() {
    Thread threadLogging = new Thread(new Runnable() {
      public void run() {
        // stop and write metric
        if (statsd != null) {
          statsd.stop(statusResponse);
        }
        
        //write logging
        LoggingProcessor.writeLogging(logObject, Configuration.getLoggingRule());
      }
    });
    threadLogging.start();
  }
  
  /**
   * Authenticate filter.
   *
   * @return true, if authenticate success
   */
  private boolean authenticateFilter(){
    boolean res = true;
    String username = this.request.headers().get(Constants.USERNAME_PRO);
    String password = this.request.headers().get(Constants.PASSWORD_PRO);
    if (Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(password)) {
      logger.info("authenticate failed");
      isException = true;
      exception = new BadRequestException(new Exception("authenticate failed"));
      res = false;
    } else {
      if (!Constants.AUTH_STR.equalsIgnoreCase(username)
          || !Constants.AUTH_STR.equalsIgnoreCase(password)) {
        logger.info("authenticate failed");
        isException = true;
        exception = new BadRequestException(new Exception("authenticate failed"));
        res = false;
      }
    }
    return res;
  }
  
  /**
   * Ip filter.
   *
   * @param address the address
   * @return true, if ip is not restricted
   */
  private boolean ipFilter(String address){
    boolean res = true;
    String tmp = address.split(":")[0].replace("/", "").trim();
    if (Configuration.getIpFilterRule().getHosts().contains(tmp)) {
      logger.info("ipfilter restrict");
      isException = true;
      exception = new BadRequestException(new Exception("ipfilter restrict"));
      res = false;
    }
    return res;
  }
  
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.error("Exception caught", cause);

    HttpResponseStatus status =
        (cause instanceof BadRequestException) ? BAD_REQUEST : INTERNAL_SERVER_ERROR;

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    cause.printStackTrace(printWriter);
    String content = stringWriter.toString();

    FullHttpResponse response =
        new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(content,
            CharsetUtil.UTF_8));

    response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
    response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

    ctx.writeAndFlush(response);
    ctx.close();
  }
}
