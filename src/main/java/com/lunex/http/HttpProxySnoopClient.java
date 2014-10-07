package com.lunex.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.util.HostAndPort;

public class HttpProxySnoopClient {

  static final Logger logger = LoggerFactory.getLogger(HttpProxySnoopClient.class);

  private HostAndPort address;
  public CallbackHTTPVisitor callback;
  public Object msg;
  public Channel ch;
  public EventLoopGroup group;
  public HttpProxySnoopClient(HostAndPort address, CallbackHTTPVisitor callback) {
    this.address = address;
    this.callback = callback;
  }


  /**
   * Submit request to target
   * 
   * @param request
   * @param requestContent
   * @return
   * @throws Exception
   */
  public Channel submitRequest(HttpRequest request, HttpContent requestContent) throws Exception {
    try {
      if (address==null || address.getHost()==null || address.getPort()<=0) {
        return null;
      }
    } catch (Exception ex) {
      throw ex;
    }

    // Configure the client.
    group = new NioEventLoopGroup(1);
    try {
      Bootstrap b = new Bootstrap();
      b.group(group).channel(NioSocketChannel.class)
      .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 10 * 64 * 1024)
      .option(ChannelOption.TCP_NODELAY, true)
      .option(ChannelOption.SO_SNDBUF, 1048576)
      .option(ChannelOption.SO_RCVBUF, 1048576)
      .handler(new HttpProxySnoopClientInitializer(callback));
      // Make the connection attempt.
      ch = b.connect(address.getHost(), address.getPort()).sync().channel();
      // Send the HTTP request.
      HttpRequest temp =
          new DefaultFullHttpRequest(request.getProtocolVersion(), request.getMethod(),
              address.getUrl() + request.getUri(), requestContent.content());
      temp.headers().add(request.headers());
      temp.headers().set("Host", address.getHost());
      ch.writeAndFlush(temp);
      
    } catch (Exception ex) {
      throw ex;
    } 
    return ch;
  }

  /**
   * Shutdown
   */
  public void shutdown() {
    ch.disconnect();
    group.shutdownGracefully();
  }
}
