package com.lunex.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

import java.net.URI;
import java.net.URISyntaxException;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProxySnoopClient {

  static final Logger logger = LoggerFactory.getLogger(HttpProxySnoopClient.class);

  private String url;
  private URI uri;
  private int port;
  private String host;
  private String scheme;
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
  private boolean preProcessURL() throws URISyntaxException, SSLException {
    this.uri = new URI("http://" + url);
    this.scheme = uri.getScheme();
    this.host = uri.getHost();
    this.port = uri.getPort();
    if (port == -1) {
      if ("http".equalsIgnoreCase(scheme)) {
        port = 80;
      } else if ("https".equalsIgnoreCase(scheme)) {
        port = 443;
      }
    }

    if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
      System.err.println("Only HTTP(S) is supported.");
      return false;
    }

    return true;
  }

  public Channel submitRequest(HttpRequest request) throws Exception {
    try {
      if (!this.preProcessURL()) {
        return null;
      }
    } catch (URISyntaxException ex) {
      throw ex;
    } catch (SSLException ex) {
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
      if (request == null) {
        request =
            new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
        request.headers().set(HttpHeaders.Names.HOST, host);
        request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
        request.headers().set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);
        request.headers().set(HttpHeaders.Names.ACCEPT_CHARSET, "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
      }
      ch.writeAndFlush(request);

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
