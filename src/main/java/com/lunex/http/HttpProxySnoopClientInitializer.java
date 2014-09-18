package com.lunex.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;

/**
 * Channel initualizer for netty client
 * 
 * @author BaoLe
 *
 */
public class HttpProxySnoopClientInitializer extends ChannelInitializer<SocketChannel> {

  private CallbackHTTPVisitor callback;

  public HttpProxySnoopClientInitializer(CallbackHTTPVisitor callback) {
    this.callback = callback;
  }

  @Override
  public void initChannel(SocketChannel ch) {
    ChannelPipeline p = ch.pipeline();

    p.addLast(new HttpClientCodec());

    // Remove the following line if you don't want automatic content
    // decompression.
    p.addLast(new HttpContentDecompressor());

    // Uncomment the following line if you don't want to handle
    // HttpContents.
    // p.addLast(new HttpObjectAggregator(1048576));

    p.addLast(new HttpProxySnoopClientHandler(callback));
  }
}
