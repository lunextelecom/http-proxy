package com.lunex.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;

// TODO: Auto-generated Javadoc
/**
 * Handler for netty client.
 */
public class HttpProxySnoopClientHandler extends SimpleChannelInboundHandler<HttpObject> {

  /** The Constant logger. */
  static final Logger logger = LoggerFactory.getLogger(HttpProxySnoopClientHandler.class);

  /** The callback. */
  private CallbackHTTPVisitor callback;

  /**
   * The Constructor.
   *
   * @param callback the callback
   */
  public HttpProxySnoopClientHandler(CallbackHTTPVisitor callback) {
    this.callback = callback;
  }

  /* (non-Javadoc)
   * @see io.netty.channel.ChannelInboundHandlerAdapter#channelReadComplete(io.netty.channel.ChannelHandlerContext)
   */
  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }

  /* (non-Javadoc)
   * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
   */
  @Override
  public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
    if (msg instanceof LastHttpContent || msg instanceof HttpContent || msg instanceof HttpResponse) {
      if (callback != null) {
        callback.doJob(ctx, msg);
      }
    }
  }

  /* (non-Javadoc)
   * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
