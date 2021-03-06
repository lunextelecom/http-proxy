package com.lunex.httpproxy.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.httpproxy.util.Configuration;

/**
 * Netty server for http protocol
 *
 */
public class HttpProxyServer {

  static final Logger logger = LoggerFactory.getLogger(HttpProxyServer.class);

  private int port;
  private ServerBootstrap bootStrap;
  private Channel channel;
  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;

  public HttpProxyServer(int port) {
    this.port = port;
  }

  /**
   * Start HTTP server to listen request from client
   * 
   * @throws Exception
   */
  public synchronized void startServer() throws Exception {

    // Configure the server.
    
    bossGroup = new NioEventLoopGroup(Configuration.proxyNumThread);
    workerGroup = new NioEventLoopGroup(Configuration.proxyNumThread);
    try {
      bootStrap = new ServerBootstrap();
      bootStrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childOption(ChannelOption.TCP_NODELAY, true)
          .childOption(ChannelOption.SO_KEEPALIVE, true)
          .childHandler(new HttpProxyServerInitializer());
      channel = bootStrap.bind(port).sync().channel();

      ChannelFuture channelFuture = channel.closeFuture();
      channelFuture.sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }

  /**
   * Shutdown server
   */
  public synchronized void stopServer() {
    channel.close();
    bossGroup.shutdownGracefully();
    workerGroup.shutdownGracefully();
  }
}
