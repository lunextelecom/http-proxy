package com.lunex.http;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.App;
import com.lunex.rule.MetricRulePattern;
import com.lunex.rule.RoutingRulePattern;
import com.lunex.util.HostAndPort;
import com.lunex.util.LogObject;
import com.lunex.util.LoggingProcessor;
import com.lunex.util.Constants.EVerb;
import com.lunex.util.ParameterHandler;
import com.lunex.util.Statsd;

public class HttpProxySnoopServerHandler extends SimpleChannelInboundHandler<HttpObject> {

  static final Logger logger = LoggerFactory.getLogger(HttpProxySnoopServerHandler.class);
  private LastHttpContent trailer;
  private RoutingRulePattern routingRulePattern;
  private MetricRulePattern metricRulePattern;
  private Statsd statsd;
  private String statusResponse;

  private HttpRequest request;
  private HttpContent requestContent;
  private DefaultHttpResponse defaultHttpResponse;
  private DefaultLastHttpContent defaultLastHttpContent;
  private final StringBuilder responseContentBuilder = new StringBuilder();

  private LogObject logObject;

  public HttpProxySnoopServerHandler() {}


  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    // write response
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
      routingRulePattern = App.routingRule.selectRulePattern(this.request);
      responseContentBuilder.setLength(0);
      logObject = new LogObject();
      logObject.setRequest(this.request);
      logObject.setRequestHeaders(((HttpRequest) msg).headers());
      logObject.setMethod(EVerb.valueOf(this.request.getMethod().toString()));
      Channel channel = ctx.channel();
      SocketAddress address = channel.remoteAddress();
      logObject.setClient(address.toString());
    }

    if (msg instanceof HttpContent) {
      HttpContent httpContent = (HttpContent) msg;
      logObject.setRequestContent(httpContent);
      requestContent = httpContent;

      if (msg instanceof LastHttpContent) {
        trailer = (LastHttpContent) msg;
        if (!trailer.trailingHeaders().isEmpty()) {
          // TODO
        }

        // call target to request
        this.processCallTarget();
      }
    }
  }

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
        // TODO Auto-generated method stub
        super.doJob(ctx, msg);
        logger.info(msg.toString());
        responseWaiter.countDown();
        if (msg instanceof DefaultHttpResponse) {
          defaultHttpResponse = (DefaultHttpResponse) msg;
          HttpResponseStatus st = defaultHttpResponse.getStatus();
          Integer code = st.code();
          statusResponse = code.toString();
          return;
        }
        if (msg instanceof DefaultLastHttpContent) {
          defaultLastHttpContent = (DefaultLastHttpContent) msg;
        }
        if (msg instanceof HttpContent) {
          logObject.setResponseContent(defaultLastHttpContent);
          HttpContent httpContent = (HttpContent) msg;
          ByteBuf content = httpContent.content();
          if (content.isReadable()) {
            String dataContent = content.toString(CharsetUtil.UTF_8);
            responseContentBuilder.append(dataContent);
          }
        }
      }
    });
    try {
      // start write metric
      metricRulePattern = App.metricRule.selectRulePattern(this.request);
      if (metricRulePattern != null) {
        String metric =
            metricRulePattern.getMetric().replace("target.",
                targetUrl.replace(".", "_").replace(":", "_") + ".");
        statsd = Statsd.start(metric, ParameterHandler.METRIC_HOST, ParameterHandler.METRIC_PORT);
      }

      client.submitRequest(request, requestContent);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    try {
      responseWaiter.await();
      client.shutdown();
      logger.info("Shutdown client");
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
    }
  }

  private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
    logger.info("Write response");
    // Decide whether to close the connection or not.
    boolean keepAlive = isKeepAlive(request);

    // Build the response object.
    FullHttpResponse response =
        new DefaultFullHttpResponse(defaultHttpResponse.getProtocolVersion(), defaultHttpResponse.getStatus(),
            Unpooled.copiedBuffer(responseContentBuilder.toString(), CharsetUtil.UTF_8));

    response.headers().set(CONTENT_TYPE, defaultHttpResponse.headers().get("Content-Type"));

    if (keepAlive) {
      // Add 'Content-Length' header only for a keep-alive connection.
      response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
      // Add keep alive header as per:
      // http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
      response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
    }

    // Encode the cookie.
    // String cookieString = request.headers().get(COOKIE);
    // if (cookieString != null) {
    // Set<Cookie> cookies = CookieDecoder.decode(cookieString);
    // if (!cookies.isEmpty()) {
    // // Reset the cookies if necessary.
    // for (Cookie cookie : cookies) {
    // response.headers().add(SET_COOKIE, ServerCookieEncoder.encode(cookie));
    // }
    // }
    // }

    // Write the response.
    ctx.writeAndFlush(response);

    return keepAlive;
  }

  private void processLogging() {
    Thread threadLogging = new Thread(new Runnable() {
      public void run() {
        LoggingProcessor.writeLogging(logObject, App.loggingRule);// write logging

        if (statsd != null) {// stop and write metric
          statsd.stop(statusResponse);
        }

      }
    });
    threadLogging.start();
  }
}
