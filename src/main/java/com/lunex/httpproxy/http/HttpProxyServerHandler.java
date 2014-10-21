package com.lunex.httpproxy.http;

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
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.lunex.httpproxy.balancing.BalancingStrategy;
import com.lunex.httpproxy.exceptions.BadRequestException;
import com.lunex.httpproxy.exceptions.InternalServerErrorException;
import com.lunex.httpproxy.rule.RouteInfo;
import com.lunex.httpproxy.rule.RouteInfo.Verb;
import com.lunex.httpproxy.rule.ServerInfo;
import com.lunex.httpproxy.util.Configuration;
import com.lunex.httpproxy.util.HostAndPort;
import com.lunex.httpproxy.util.LogObject;
import com.lunex.httpproxy.util.LogObjectQueue;
import com.lunex.httpproxy.util.MetricObjectQueue;

/**
 * Server handler for netty server
 */
public class HttpProxyServerHandler extends SimpleChannelInboundHandler<HttpObject> {

  static final Logger logger = LoggerFactory.getLogger(HttpProxyServerHandler.class);
  private RouteInfo selectedRoute;
  private long metricStartTime = 0;
  private String statusResponse;

  private HttpRequest request;
  
  private HttpMethod method;
  private Boolean isException = false;
  private Exception exception;
  private HttpContent requestContent;
  private DefaultHttpResponse defaultHttpResponse;
  private final StringBuilder responseContentBuilder = new StringBuilder();

  private LogObject logObject;

  public HttpProxyServerHandler() {}

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    if(request != null){
      // write response
      if (isException) {
        exceptionCaught(ctx, exception);
        return;
      }
      if (!writeResponse(ctx)) {
        // If keep-alive is off, close the connection once the content is fully written.
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
      } else {
        ctx.flush();
      }
      ctx.disconnect();
      if(selectedRoute != null){
        this.processLogging();
      }
    }
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
    if (msg instanceof HttpRequest) {
      this.request = (HttpRequest) msg;
      Channel channel = ctx.channel();
      SocketAddress address = channel.remoteAddress();

      if(Configuration.getMapUrlRoute().containsKey(this.request.getUri())){
        selectedRoute = Configuration.getMapUrlRoute().get(this.request.getUri());
      }else{
        selectedRoute = Configuration.getProxyRule().selectRouteInfo(this.request.getUri());
        if(selectedRoute!=null){
          Configuration.getMapUrlRoute().put(this.request.getUri(), selectedRoute);
        }
      }
      if(selectedRoute != null){
        responseContentBuilder.setLength(0);
        logObject = new LogObject();
        logObject.setRequest(this.request.getUri());
        logObject.setRequestHeaders(((HttpRequest) msg).headers().entries().toString());
        logObject.setMethod(Verb.valueOf(this.request.getMethod().toString()));
        logObject.setClient(address.toString());
      }else{
        isException = true;
        exception = new InternalServerErrorException(new Exception("Can't find any available server for this request"));
        return;
      }
    }
    
    if (msg instanceof HttpContent) {
      if (!isException) {
        HttpContent httpContent = (HttpContent) msg;
        logObject.setRequestContent("");
        if (httpContent.content() != null && httpContent.content().isReadable()) {
          String dataContent = httpContent.content().toString(CharsetUtil.UTF_8);
          logObject.setRequestContent(dataContent);
        }
        requestContent = httpContent;
      }
    }
    if (msg instanceof LastHttpContent) {
      // call target to request
      this.processCallTarget();
    }
  }

  /**
   * Submit request to target from Balancer
   * 
   */
  private void processCallTarget() {
    if (request == null) {
      return;
    }
    if (selectedRoute == null) {
      return;
    }
    method = request.getMethod();
    final CountDownLatch responseWaiter = new CountDownLatch(1);
    ServerInfo selectedServer = Configuration.getProxyRule().selectServerInfo(selectedRoute);
    HostAndPort target = null;
    if (selectedServer != null) {
      BalancingStrategy balancing = selectedServer.getBalancingStrategy();
      if(balancing != null){
        target = balancing.selectTarget(selectedServer.getTargets());
      }
    }
    if (target == null) {
      logger.error("target is null");
      responseContentBuilder.append("Target is null");
      isException = true;
      exception = new InternalServerErrorException(new Exception("Can't find any available server for this request"));
      return;
    }
    logObject.setTarget(target.toString());

    HttpProxyClient client = new HttpProxyClient(target, new CallbackHTTPVisitor() {
      @Override
      public void doJob(ChannelHandlerContext ctx, HttpObject msg) {
        super.doJob(ctx, msg);
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
        if (msg instanceof LastHttpContent) {
          responseWaiter.countDown();
        }
      }
    });
    try {
      // start write metric
      String metric = selectedRoute.getMetric();
      if (!Strings.isNullOrEmpty(metric)) {
        metricStartTime = System.currentTimeMillis();
      }
      client.submitRequest(request, requestContent);
    } catch (Exception e) {
      responseWaiter.countDown();
      isException = true;
      exception = new BadRequestException(e);
      logger.error(e.getMessage());
    }
    try {
      responseWaiter.await(60, TimeUnit.SECONDS);
      client.shutdown();
      logger.info("Shutdown client");
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * Write response from http client to client of netty server
   * 
   * @param currentObj
   * @param ctx
   * @return
   */
  private boolean writeResponse(ChannelHandlerContext ctx) {
    logger.info("Write response");
    // Decide whether to close the connection or not.
    boolean keepAlive = false;
    try {
      keepAlive = isKeepAlive(request);
    } catch (Exception e) {
      keepAlive = false;
    }
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
   */
  private void processLogging() {
    //write metric
    if(Configuration.getProducer()!=null){
      if (metricStartTime > 0) {
        String metric = selectedRoute.getMetric();
        if(!Strings.isNullOrEmpty(metric)){
          MetricObjectQueue obj = new MetricObjectQueue();
          metric = metric.replace("{server_name}", selectedRoute.getServer())
              .replace("{verb}", selectedRoute.getVerd().toString())
              .replace("{route_name}", selectedRoute.getName())
              .replace("{response_code}", statusResponse);
          obj.setMetric(metric);
          obj.setMetricStartTime(metricStartTime);
          obj.setMetricStopTime(System.currentTimeMillis());
          obj.setStatusResponse(statusResponse);
          try {
            logger.info("send MetricObjectQueue message");
            Configuration.getProducer().sendMessage(obj);
          } catch (IOException e) {
            logger.error("sendMessage error ", e);
          }
        }
      }
      //write logging
      if(!Configuration.getProxyRule().isOffLogging(selectedRoute)){
        LogObjectQueue obj = new LogObjectQueue();
        obj.setLogObject(logObject);
        obj.setMethodName(method.name());
        obj.setSelectedRoute(selectedRoute);
        try {
          logger.info("send LogObjectQueue message");
          Configuration.getProducer().sendMessage(obj);
        } catch (IOException e) {
          logger.error("sendMessage error ", e);
        }
      }
    }
  }
  
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.error("Exception caught", cause);

    HttpResponseStatus status =  (cause instanceof BadRequestException) ? BAD_REQUEST : INTERNAL_SERVER_ERROR;
    String content = cause.getMessage();
    FullHttpResponse response =
        new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(content,
            CharsetUtil.UTF_8));

    response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
    response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

    ctx.writeAndFlush(response);
    ctx.close();
  }
}
