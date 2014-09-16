package com.lunex;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.http.HttpProxySnoopServer;
import com.lunex.rule.RoutingRule;
import com.lunex.util.Configuration;

/**
 * Main Class
 * 
 * @author BaoLe
 *
 */
public class App {

  static final Logger logger = LoggerFactory.getLogger(App.class);

  public static Map<String, Object> config;
  public static RoutingRule routingRule;

  public static void main(String[] args) {

    App.loadLog4j();

    App.loadRoutingRule();

    App.startHttpProxy();
  }

  public static void loadLog4j() {
    // load log properties
    Properties props = new Properties();
    try {
      props.load(new FileInputStream("log4j.properties"));
      PropertyConfigurator.configure(props);
    } catch (IOException e2) {
      e2.printStackTrace();
    }
  }

  /**
   * Load routing rule from configuration.yaml
   * 
   * @author BaoLe
   */
  public static void loadRoutingRule() {
    Configuration.initBalancerStrategy();
    config = null;
    try {
      config = Configuration.loadYamlFile("configuration.yaml");
    } catch (Exception e1) {
      logger.error(e1.getMessage());
    }
    if (config == null) {
      return;
    }
    List<Map<String, Object>> routingRuleArray = (List<Map<String, Object>>) config.get("Routing");

    try {
      routingRule = new RoutingRule();
      routingRule.loadRoutingRule(routingRuleArray);
    } catch (Exception e) {
      logger.error(e.getMessage());
      routingRule = null;
    }
  }

  /**
   * Start netty server as HTTP proxy
   * 
   * @author BaoLe
   */
  public static void startHttpProxy() {
    if (routingRule == null) {
      logger.error("Can not load config or config invalid", new NullPointerException());
      return;
    }
    final HttpProxySnoopServer server = new HttpProxySnoopServer(8080, routingRule);
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
