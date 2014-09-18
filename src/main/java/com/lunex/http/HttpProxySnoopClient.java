package com.lunex.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProxySnoopClient {

  static final Logger logger = LoggerFactory.getLogger(HttpProxySnoopClient.class);

  private String url;
  private URI uri;
  private int port;
  private String host;
  public CallbackHTTPVisitor callback;
  public Object msg;
  public Channel ch;
  public EventLoopGroup group;

  public HttpProxySnoopClient(String url, CallbackHTTPVisitor callback) {
    this.url = url;
    this.callback = callback;
  }

  /**
   * prepare host information for processing
   * 
   * @return
   * @throws URISyntaxException
   * @throws SSLException
   */
  private boolean preProcessURL() {
    try {
      String[] array = url.split(":");
      this.host = array[0];
      this.port = Integer.valueOf(array[1]);
    } catch (Exception ex) {
      return false;
    }
    return true;
  }

  public Channel submitRequest(HttpRequest request, HttpContent requestContent) throws Exception {
    try {
      if (!this.preProcessURL()) {
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
      ch = b.connect(host, port).sync().channel();

      // Send the HTTP request.
      HttpRequest temp = new DefaultFullHttpRequest(request.getProtocolVersion(), request.getMethod(), request.getUri(), requestContent.content());
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

  public void shutdown() {
    group.shutdownGracefully();
  }
}
