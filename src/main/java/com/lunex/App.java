package com.lunex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.http.HttpProxySnoopServer;
import com.lunex.scheduler.JobScheduler;
import com.lunex.util.Configuration;


public class App {

  static final Logger logger = LoggerFactory.getLogger(App.class);
  private static HttpProxySnoopServer server;
 
  public static void main(String[] args) {
    Configuration.loadConfig("log4j.properties", "app.properties", "configuration.yaml");
    App.startHttpProxy();
    JobScheduler.run();
    logger.info("startup done, listening....");
  }


  /**
   * Start netty server as HTTP proxy
   * 
   */
  public static void startHttpProxy() {
    if (Configuration.getProxyRule().getRoutes() == null || Configuration.getProxyRule().getRoutes().isEmpty() || Configuration.getProxyPort() <= 0) {
      logger.error("Can not load config or config invalid", new NullPointerException());
      return;
    }
    server = new HttpProxySnoopServer(Configuration.getProxyPort());
    Thread thread = new Thread(new Runnable() {

      public void run() {
        try {
          server.startServer();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    thread.start();
  }
  

}
