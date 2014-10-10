package com.lunex.httpproxy.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;

/**
 * Callback for response get from client handler
 * 
 */

public class CallbackHTTPVisitor {

  public void doJob(ChannelHandlerContext ctx, HttpObject msg) {
    return;
  }
}
