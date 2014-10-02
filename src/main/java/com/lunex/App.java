package com.lunex;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.http.HttpProxySnoopServer;
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
  
  /**
   * The main method.
   *
   * @param args the args
   */
  public static void main(String[] args) {
    // load log properties
    Properties props = new Properties();
    try {
      props.load(new FileInputStream("log4j.properties"));
      PropertyConfigurator.configure(props);
    } catch (IOException ex) {
      logger.error(ex.getMessage());
    }
    
    Configuration.loadConfig("app.properties");
    startHttpProxy();
//    JobScheduler.run();
//    startAutoReloadConfig();
    logger.info("startup done, listening....");
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
   * Start auto reload config.
   */
  public static void startAutoReloadConfig() {
    try {
      WatchService watcher = FileSystems.getDefault().newWatchService();
      Path dir = Paths.get(Configuration.proxyConfigDir);
      dir.register(watcher, ENTRY_MODIFY);

      logger.info("Watch Service registered for dir: " + dir.getFileName());
      boolean isExit = false;
      while (true) {
        WatchKey key;
        try {
          key = watcher.take();
        } catch (InterruptedException ex) {
          return;
        }

        for (WatchEvent<?> event : key.pollEvents()) {
          WatchEvent.Kind<?> kind = event.kind();

          @SuppressWarnings("unchecked")
          WatchEvent<Path> ev = (WatchEvent<Path>) event;
          Path fileName = ev.context();

          if (kind == ENTRY_MODIFY && fileName.toString().equals(Configuration.proxyConfigName)) {
            Configuration.reloadConfig();
            isExit = true;
            logger.info("reload config done");
            break;
          }
        }
        if(isExit){
          break;
        }
        boolean valid = key.reset();
        if (!valid) {
          break;
        }
      }
      startAutoReloadConfig();

    } catch (IOException ex) {
      logger.error("AutoReload error :", ex);
    }
  }

}
