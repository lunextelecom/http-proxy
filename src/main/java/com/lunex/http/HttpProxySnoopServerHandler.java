package com.lunex.http;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.App;
import com.lunex.util.HostAndPort;
import com.lunex.util.RoutingRulePattern;

public class HttpProxySnoopServerHandler extends SimpleChannelInboundHandler<Object> {

  static final Logger logger = LoggerFactory.getLogger(HttpProxySnoopServerHandler.class);
  private LastHttpContent trailer;
  private RoutingRulePattern routingRulePattern;

  private HttpRequest request;
  private DefaultHttpResponse defaultHttpResponse;
  private DefaultLastHttpContent defaultLastHttpContent;
  private final StringBuilder responseContentBuilder = new StringBuilder();

  public HttpProxySnoopServerHandler() {
  }


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
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
    if (msg instanceof HttpRequest) {
      this.request = (HttpRequest) msg;
      routingRulePattern = selectRulePattern(this.request);
      responseContentBuilder.setLength(0);
    }

    if (msg instanceof HttpContent) {
      if (msg instanceof LastHttpContent) {
        trailer = (LastHttpContent) msg;
        if (!trailer.trailingHeaders().isEmpty()) {
        }

        // TODO something with jsonObject and return response
        final CountDownLatch responseWaiter = new CountDownLatch(1);
        HostAndPort target = routingRulePattern.getBalancingStrategy().selectTarget();
        if (target == null) {
          logger.error("target is null");
          responseContentBuilder.append("Target is null");
          return;
        }
        HttpProxySnoopClient client =
            new HttpProxySnoopClient(target.getHost() + ":" + target.getPort(),
                new CallbackHTTPVisitor() {
                  @Override
                  public void doJob(ChannelHandlerContext ctx, HttpObject msg) {
                    // TODO Auto-generated method stub
                    super.doJob(ctx, msg);
                    logger.info(msg.toString());
                    responseWaiter.countDown();
                    if (msg instanceof DefaultHttpResponse) {
                      defaultHttpResponse = (DefaultHttpResponse) msg;
                      return;
                    }
                    if (msg instanceof DefaultLastHttpContent) {
                      defaultLastHttpContent = (DefaultLastHttpContent) msg;
                    }
                    if (msg instanceof HttpContent) {
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
          client.submitRequest(request);
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
    }
  }

  private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
    logger.info("Write response");
    // Decide whether to close the connection or not.
    boolean keepAlive = isKeepAlive(request);

    // Build the response object.
    FullHttpResponse response =
        new DefaultFullHttpResponse(HTTP_1_1, currentObj.getDecoderResult().isSuccess() ? OK
            : BAD_REQUEST, Unpooled.copiedBuffer(responseContentBuilder.toString(),
            CharsetUtil.UTF_8));

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

  public RoutingRulePattern selectRulePattern(HttpRequest request) {
    for (int i = 0; i < App.routingRule.getListRulePattern().size(); i++) {
      RoutingRulePattern rule = App.routingRule.getListRulePattern().get(i);
      Pattern r = Pattern.compile(rule.getRegexp());
      Matcher m = r.matcher(request.getUri());
      if (m.find())
        return rule;
    }
    return null;
  }

}
