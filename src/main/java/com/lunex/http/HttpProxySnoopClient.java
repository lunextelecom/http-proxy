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

import java.net.SocketAddress;
import java.net.URISyntaxException;

import javax.net.ssl.SSLException;

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
    group = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();
      b.group(group).channel(NioSocketChannel.class)
          .handler(new HttpProxySnoopClientInitializer(callback));
      // Make the connection attempt.
      ch = b.connect(address.getHost(), address.getPort()).sync().channel();
      // Send the HTTP request.
      HttpRequest temp =
          new DefaultFullHttpRequest(request.getProtocolVersion(), request.getMethod(),
              address.getUrl() + request.getUri(), requestContent.content());
      temp.headers().add(request.headers());
      ch.writeAndFlush(temp);

      // Wait for the server to close the connection.
      // ch.closeFuture().sync();
    } catch (Exception ex) {
      throw ex;
    } finally {
      // Shut down executor threads to exit.
      // group.shutdownGracefully();
    }
    return ch;
  }

  /**
   * Shutdown
   * 
   * @author BaoLe
   */
  public void shutdown() {
    group.shutdownGracefully();
  }
}
