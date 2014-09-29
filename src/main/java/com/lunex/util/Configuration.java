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
import java.util.regex.Pattern;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Strings;
import com.lunex.cassandra.CassandraRepository;
import com.lunex.enums.EBalancingType;
import com.lunex.rule.ProxyRule;
import com.lunex.rule.RouteInfo;
import com.lunex.rule.ServerInfo;

/**
 * Configuration of System
 * 
 */
public class Configuration {

  static final Logger logger = LoggerFactory.getLogger(Configuration.class);

  public static Map<String, EBalancingType> MAP_BALANCER_STATEGY;
  static {
      MAP_BALANCER_STATEGY = new HashMap<String, EBalancingType>();
      MAP_BALANCER_STATEGY.put("RR", EBalancingType.ROUND_ROBIN);
  }
  private static ProxyRule proxyRule = new ProxyRule();
  private static String host = "localhost";
  private static String keyspace = "http_proxy";
  private static String configFilename = "configuration";
  
  private static int proxyPort = 8080;
  
  private static Pattern reloadPattern = Pattern.compile("/http_proxy/reload_config");
  private static Pattern targetPattern = Pattern.compile("([^:^/]*):(\\d*)?(.*)?");
  private static Pattern loggingPattern = Pattern.compile("(?i)(resp_header|resp_body|req_header|req_body|off|req)\\s*(([\"(\"])((post|put|get|delete|,|\\s*|\\*)*)([\")\"]))*(,)?\\s*");
  private static Pattern routeUrlPattern = Pattern.compile("([a-zA-Z*]+)\\s+(.*)");

  /**
   * Load config from yaml file
   * 
   * @param filePath
   * @return
   * @throws Exception
   */
  public static Map<String, Object> loadYamlFile(String filePath) throws Exception {
    File file = new File(filePath);
    InputStream inputStream = new FileInputStream(file);
    Yaml yaml = new Yaml();
    Map<String, Object> data = (Map<String, Object>) yaml.load(inputStream);
    return data;
  }

  public static void loadConfig(String log4jFilename, String appFileName, String configFilename){
    // load log properties
    Properties props = new Properties();
    try {
      props.load(new FileInputStream(log4jFilename));
      PropertyConfigurator.configure(props);
    } catch (IOException ex) {
      logger.error(ex.getMessage());
    }
    //load cassandra
    try {
      ParameterHandler.getPropertiesValues(appFileName);
      Configuration.proxyPort = ParameterHandler.HTTP_PROXY_PORT;
      Configuration.host = ParameterHandler.DB_HOST;
      Configuration.keyspace = ParameterHandler.DB_DBNAME;
      CassandraRepository.getInstance();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
    //load config
    Configuration.configFilename = configFilename;
    reloadConfig();
  }

  public static void reloadConfig() {
    String configFilename = Configuration.getConfigFilename();
    if(Strings.isNullOrEmpty(configFilename)){
      return;
    }
    // read config
    Map<String, Object> config = null;
    try {
      config = Configuration.loadYamlFile(configFilename);
    } catch (Exception e1) {
      e1.printStackTrace();
      logger.error(e1.getMessage());
    }
    if (config == null) {
      return;
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
  }

  /*get, set*/

  public static ProxyRule getProxyRule() {
    return proxyRule;
  }

  public static String getHost() {
    return host;
  }

  public static String getKeyspace() {
    return keyspace;
  }

  public static Pattern getReloadPattern() {
    return reloadPattern;
  }

  public static Pattern getTargetPattern() {
    return targetPattern;
  }

  public static Pattern getLoggingPattern() {
    return loggingPattern;
  }
  public static Pattern getRouteUrlPattern() {
    return routeUrlPattern;
  }

  public static int getProxyPort() {
    return proxyPort;
  }
  
  public static String getConfigFilename() {
    return configFilename;
  }
}
