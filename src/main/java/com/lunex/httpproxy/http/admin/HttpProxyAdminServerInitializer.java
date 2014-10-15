package com.lunex.httpproxy.http.admin;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpProxyAdminServerInitializer extends ChannelInitializer<SocketChannel> {

  /**
   * The Constructor.
   */
  public HttpProxyAdminServerInitializer() {}

  /* (non-Javadoc)
   * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
   */
  @Override
  public void initChannel(SocketChannel ch) {
    ChannelPipeline p = ch.pipeline();
    p.addLast(new HttpRequestDecoder());
    p.addLast(new HttpResponseEncoder());
    p.addLast(new HttpProxyAdminServerHandler());
  }
}
