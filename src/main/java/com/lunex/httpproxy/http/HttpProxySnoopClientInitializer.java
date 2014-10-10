package com.lunex.httpproxy.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;

// TODO: Auto-generated Javadoc
/**
 * Channel initualizer for netty client.
 */
public class HttpProxySnoopClientInitializer extends ChannelInitializer<SocketChannel> {

  /** The callback. */
  private CallbackHTTPVisitor callback;

  /**
   * The Constructor.
   *
   * @param callback the callback
   */
  public HttpProxySnoopClientInitializer(CallbackHTTPVisitor callback) {
    this.callback = callback;
  }

  /* (non-Javadoc)
   * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
   */
  @Override
  public void initChannel(SocketChannel ch) {
    ChannelPipeline p = ch.pipeline();
    p.addLast(new HttpClientCodec());
    p.addLast(new HttpContentDecompressor());
    p.addLast(new HttpProxySnoopClientHandler(callback));
  }
}
