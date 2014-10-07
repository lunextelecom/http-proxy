package com.lunex;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.http.HttpProxySnoopServer;
import com.lunex.http.admin.HttpProxyAdminServer;
import com.lunex.scheduler.JobScheduler;
import com.lunex.util.Configuration;


/**
 * The Class App.
 */
public class App {

  /** The Constant logger. */
  static final Logger logger = LoggerFactory.getLogger(App.class);
  
  /** The server. */
  private static HttpProxySnoopServer server;
  
  /** The admin */
  private static HttpProxyAdminServer admin;
  
  /**
   * The main method.
   *
   * @param args the args
   */
  public static void main(String[] args) {
    // load log properties
    Properties props = new Properties();
    try {
      props.load(new FileInputStream("src/main/resource/log4j.properties"));
      PropertyConfigurator.configure(props);
    } catch (IOException ex) {
      logger.error(ex.getMessage());
    }
    
    try {
      Configuration.loadConfig("src/main/resource/app.properties");
      startHttpProxy();
      startAdmin();
//      JobScheduler.run();
      logger.info("startup done, listening....");
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    
  }

  /**
   * Start netty server as HTTP proxy.
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
  
  /**
   * Start admin
   */
  public static void startAdmin() {
    if (Configuration.getProxyAdminPort() <= 0) {
      logger.error("Can not load config or config invalid", new NullPointerException());
      return;
    }
    admin = new HttpProxyAdminServer(Configuration.getProxyAdminPort());
    Thread thread = new Thread(new Runnable() {

      public void run() {
        try {
          admin.startServer();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    thread.start();
  }

}
