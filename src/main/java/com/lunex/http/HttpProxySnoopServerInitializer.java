package com.lunex.http;

import com.lunex.rule.RoutingRule;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpProxySnoopServerInitializer extends ChannelInitializer<SocketChannel> {

  private RoutingRule routingRule;

  public HttpProxySnoopServerInitializer(RoutingRule routingRule) {
    this.routingRule = routingRule;
  }

  @Override
  public void initChannel(SocketChannel ch) {
    ChannelPipeline p = ch.pipeline();
    p.addLast(new HttpRequestDecoder());
    p.addLast(new HttpResponseEncoder());
    p.addLast(new HttpProxySnoopServerHandler(routingRule));
  }
}
