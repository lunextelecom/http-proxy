package com.lunex.http.admin;

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

/**
 * Netty server for http protocol
 *
 */
public class HttpProxyAdminServer {

  static final Logger logger = LoggerFactory.getLogger(HttpProxyAdminServer.class);

  private int port;
  private ServerBootstrap bootStrap;
  private Channel channel;
  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;

  public HttpProxyAdminServer(int port) {
    this.port = port;
  }

  /**
   * Start HTTP server to listen request from client
   * 
   * @throws Exception
   */
  public synchronized void startServer() throws Exception {

    // Configure the server.
    
    bossGroup = new NioEventLoopGroup(1);
    workerGroup = new NioEventLoopGroup(1);
    try {
      bootStrap = new ServerBootstrap();
      bootStrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childOption(ChannelOption.TCP_NODELAY, true)
          .childOption(ChannelOption.SO_KEEPALIVE, true)
          .childHandler(new HttpProxyAdminServerInitializer());
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
