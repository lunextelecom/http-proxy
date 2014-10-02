package com.lunex.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

// TODO: Auto-generated Javadoc
/**
 * Channel Initializer for netty server.
 */
public class HttpProxySnoopServerInitializer extends ChannelInitializer<SocketChannel> {

  /**
   * The Constructor.
   */
  public HttpProxySnoopServerInitializer() {}

  /* (non-Javadoc)
   * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
   */
  @Override
  public void initChannel(SocketChannel ch) {
    ChannelPipeline p = ch.pipeline();
    p.addLast(new HttpRequestDecoder());
    p.addLast(new HttpResponseEncoder());
    p.addLast(new HttpProxySnoopServerHandler());
  }
}
