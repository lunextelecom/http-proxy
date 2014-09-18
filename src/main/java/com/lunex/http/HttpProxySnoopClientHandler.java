package com.lunex.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;

public class HttpProxySnoopClientHandler extends SimpleChannelInboundHandler<HttpObject> {

  static final Logger logger = LoggerFactory.getLogger(HttpProxySnoopClientHandler.class);

  private CallbackHTTPVisitor callback;

  public HttpProxySnoopClientHandler(CallbackHTTPVisitor callback) {
    this.callback = callback;
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
    logger.info(msg.toString());
    if (msg instanceof LastHttpContent 
        || msg instanceof HttpContent
        || msg instanceof HttpResponse) {
      if (callback != null) {
        callback.doJob(ctx, msg);
      }
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
