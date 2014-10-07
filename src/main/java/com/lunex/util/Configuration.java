package com.lunex.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Strings;
import com.lunex.cassandra.CassandraRepository;
import com.lunex.enums.EBalancingType;
import com.lunex.queue.Producer;
import com.lunex.queue.QueueConsumer;
import com.lunex.rule.ProxyRule;
import com.lunex.rule.RouteInfo;
import com.lunex.rule.ServerInfo;

// TODO: Auto-generated Javadoc
/**
 * Configuration of System.
 */
public class Configuration {

  /** The Constant logger. */
  static final Logger logger = LoggerFactory.getLogger(Configuration.class);

  /** The map url route. */
  private static Map<String, RouteInfo> mapUrlRoute = new HashMap<String, RouteInfo>();
  
  /** The map balancer stategy. */
  public static Map<String, EBalancingType> MAP_BALANCER_STATEGY;
  static {
      MAP_BALANCER_STATEGY = new HashMap<String, EBalancingType>();
      MAP_BALANCER_STATEGY.put("RR", EBalancingType.ROUND_ROBIN);
      MAP_BALANCER_STATEGY.put("LU", EBalancingType.LEAST_USE);
  }
  
  /** The proxy rule. */
  private static ProxyRule proxyRule = new ProxyRule();
  
  /** The host. */
  private static String host = "localhost";
  
  /** The keyspace. */
  private static String keyspace = "http_proxy";
  
  /** The proxy port. */
  private static int proxyPort = 8080;
  
  private static int proxyAdminPort = 9999;
  
  /** The proxy num thread. */
  public static int proxyNumThread;
  
  /** The proxy config name. */
  public static String proxyConfigName;
  
  /** The target pattern. */
  private static Pattern targetPattern = Pattern.compile("([^:|\\/^]*)(:)?(\\d*)?(.*)?");
  
  /** The logging pattern. */
  private static Pattern loggingPattern = Pattern.compile("(?i)(resp_header|resp_body|req_header|req_body|off|req)\\s*(([\"(\"])((post|put|get|delete|,|\\s*|\\*)*)([\")\"]))*(,)?\\s*");
  
  /** The route url pattern. */
  private static Pattern routeUrlPattern = Pattern.compile("([a-zA-Z*]+)\\s+(.*)");
  
  private static Pattern reloadPattern = Pattern.compile("/http_proxy/reloadconfig$");
  
  private static Pattern checkHealthPattern = Pattern.compile("/http_proxy/checkhealth$");

  /** The consumer. */
  private static QueueConsumer consumer;
  
  /** The producer. */
  private static Producer producer;

  /**
   * Inits the queue.
   */
  public static void initQueue() {
    try {
      logger.info("init queue");
      consumer = new QueueConsumer("loggingQueue");
      Thread consumerThread = new Thread(consumer);
      consumerThread.start();
      
      producer = new Producer("loggingQueue");
    } catch (Exception e) {
      logger.error("loggingQueue error", e);
    }
  }
  
  /**
   * Load config from yaml file.
   *
   * @param filePath the file path
   * @return the map< string, object>
   * @throws Exception the exception
   */
  public static Map<String, Object> loadYamlFile(String filePath) throws Exception {
    File file = new File(filePath);
    InputStream inputStream = new FileInputStream(file);
    Yaml yaml = new Yaml();
    Map<String, Object> data = (Map<String, Object>) yaml.load(inputStream);
    return data;
  }

  /**
   * Load config.
   *
   * @param appFileName the app file name
   */
  public static void loadConfig(String appFileName) throws Exception{
    //init queue
    initQueue();
    //load parameter
    ParameterHandler.getPropertiesValues(appFileName);
    Configuration.proxyPort = ParameterHandler.HTTP_PROXY_PORT;
    Configuration.proxyAdminPort = ParameterHandler.HTTP_PROXY_ADMIN_PORT;
    Configuration.host = ParameterHandler.DB_HOST;
    Configuration.keyspace = ParameterHandler.DB_DBNAME;
    Configuration.proxyNumThread = ParameterHandler.HTTP_PROXY_NUM_THREAD;
    Configuration.proxyConfigName = ParameterHandler.HTTP_PROXY_CONFIG_NAME;
    //load cassandra
    CassandraRepository.getInstance();
    //load config
    reloadConfig();
  }

  /**
   * Reload config.
   */
  public static void reloadConfig() throws Exception {
    String configFilename = Configuration.proxyConfigName;
    if(Strings.isNullOrEmpty(configFilename)){
      return;
    }
    // read config
    Map<String, Object> config = null;
    config = Configuration.loadYamlFile("src/main/resource/" + configFilename);
    if (config == null) {
      throw new Exception("config invalid");
    }
    Map<String, ServerInfo> servers = new HashMap<>();
    List<RouteInfo> routes = new ArrayList<>();
    // load server_default
    ServerInfo serverDefault = new ServerInfo();
    serverDefault.loadConfig((Map<String, String>) config.get("server_default"), null);

    // load route_default
    RouteInfo routeDefault = new RouteInfo();
    routeDefault.loadConfig((Map<String, String>) config.get("route_default"), null);

    // load server
    List<Map<String, String>> lstServerConf = (List<Map<String, String>>) config.get("servers");
    for (Map<String, String> conf : lstServerConf) {
      ServerInfo server = new ServerInfo();
      server.loadConfig(conf, serverDefault);
      servers.put(server.getName(), server);
    }
    // load route
    List<Map<String, String>> lstRouteConf = (List<Map<String, String>>) config.get("routes");
    for (Map<String, String> conf : lstRouteConf) {
      RouteInfo route = new RouteInfo();
      route.loadConfig(conf, routeDefault);
      routes.add(route);
    }
    proxyRule.setRouteDefault(routeDefault);
    proxyRule.setRoutes(routes);
    proxyRule.setServerDefault(serverDefault);
    proxyRule.setServers(servers);
    mapUrlRoute = new HashMap<String, RouteInfo>();
  }

  /*get, set*/

  /**
   * Gets the proxy rule.
   *
   * @return the proxy rule
   */
  public static ProxyRule getProxyRule() {
    return proxyRule;
  }

  /**
   * Gets the host.
   *
   * @return the host
   */
  public static String getHost() {
    return host;
  }

  /**
   * Gets the keyspace.
   *
   * @return the keyspace
   */
  public static String getKeyspace() {
    return keyspace;
  }

  /**
   * Gets the target pattern.
   *
   * @return the target pattern
   */
  public static Pattern getTargetPattern() {
    return targetPattern;
  }

  /**
   * Gets the logging pattern.
   *
   * @return the logging pattern
   */
  public static Pattern getLoggingPattern() {
    return loggingPattern;
  }
  
  /**
   * Gets the route url pattern.
   *
   * @return the route url pattern
   */
  public static Pattern getRouteUrlPattern() {
    return routeUrlPattern;
  }

  /**
   * Gets the proxy port.
   *
   * @return the proxy port
   */
  public static int getProxyPort() {
    return proxyPort;
  }
  
  /**
   * Gets the map url route.
   *
   * @return the map url route
   */
  public static Map<String, RouteInfo> getMapUrlRoute() {
    return mapUrlRoute;
  }
  
  /**
   * Gets the producer.
   *
   * @return the producer
   */
  public static Producer getProducer() {
    return producer;
  }
  
  
  public static Pattern getReloadPattern() {
    return reloadPattern;
  }

  public static int getProxyAdminPort() {
    return proxyAdminPort;
  }

  public static Pattern getCheckHealthPattern() {
    return checkHealthPattern;
  }
}
