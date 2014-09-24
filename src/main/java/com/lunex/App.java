package com.lunex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.http.HttpProxySnoopServer;
import com.lunex.scheduler.JobScheduler;
import com.lunex.util.Configuration;

/**
 * Main Class
 * 
 * @author BaoLe
 * @update DuyNguyen
 *
 */
public class App {

  static final Logger logger = LoggerFactory.getLogger(App.class);
  private static HttpProxySnoopServer server;
 
  public static void main(String[] args) {
    Configuration.loadConfig("log4j.properties", "db.properties", "configuration.yaml");
    App.startHttpProxy();
    JobScheduler.run();
    logger.info("startup done, listening....");
  }


  /**
   * Start netty server as HTTP proxy
   * 
   * @author BaoLe
   */
  public static void startHttpProxy() {
    if (Configuration.getRoutingRule() == null) {
      logger.error("Can not load config or config invalid", new NullPointerException());
      return;
    }
    server = new HttpProxySnoopServer(8080);
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
