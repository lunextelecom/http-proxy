package com.lunex.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.lunex.cassandra.CassandraRepository;
import com.lunex.rule.IpFilterRule;
import com.lunex.rule.LoggingRule;
import com.lunex.rule.MetricRule;
import com.lunex.rule.RoutingRule;
import com.lunex.util.Constants.EBalancingStrategy;

/**
 * Configuration of System
 * 
 * @author BaoLe
 * @udpate DuyNguyen
 *
 */
public class Configuration {

  static final Logger logger = LoggerFactory.getLogger(Configuration.class);

  public static Map<String, EBalancingStrategy> MAP_BALANCER_STATEGY;
  private static RoutingRule routingRule = new RoutingRule();
  private static LoggingRule loggingRule = new LoggingRule();
  private static MetricRule metricRule = new MetricRule();
  private static IpFilterRule ipFilterRule = new IpFilterRule();
  
  private static String host = "localhost";
  private static String keyspace = "http_proxy";
  
  /**
   * init list load balancer strategy for system
   * 
   * @author BaoLe
   */
  public static void initBalancerStrategy() {
    MAP_BALANCER_STATEGY = new HashMap<String, Constants.EBalancingStrategy>();
    MAP_BALANCER_STATEGY.put("RR", EBalancingStrategy.ROUND_ROBIN);
  }

  /**
   * Load config from yaml file
   * 
   * @author BaoLe
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

  public static void loadConfig(String log4jFilename, String dbFileName, String configFilename){
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
      ParameterHandler.getPropertiesValues(dbFileName);
      Configuration.setHost(ParameterHandler.DB_HOST);
      Configuration.setKeyspace(ParameterHandler.DB_DBNAME);
      CassandraRepository.getInstance();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
    //read config
    Configuration.initBalancerStrategy();
    Map<String, Object> config = null;
    try {
      config = Configuration.loadYamlFile(configFilename);
    } catch (Exception e1) {
      logger.error(e1.getMessage());
    }
    if (config == null) {
      return;
    }
    //load routingrule
    List<Map<String, Object>> routingRuleArray = (List<Map<String, Object>>) config.get("Routing");
    try {
      routingRule = new RoutingRule();
      routingRule.loadRoutingRule(routingRuleArray);
    } catch (Exception e) {
      logger.error(e.getMessage());
      routingRule = null;
    }

    //load logging rule
    List<Map<String, Object>> loggingRuleArray = (List<Map<String, Object>>) config.get("Logging");
    try {
      loggingRule = new LoggingRule();
      loggingRule.loadLoggingRule(loggingRuleArray);
    } catch (Exception e) {
      logger.error(e.getMessage());
      loggingRule = null;
    }
    
    //load metric rule
    List<Map<String, Object>> metricRuleArray = (List<Map<String, Object>>) config.get("Metric");
    try {
      metricRule = new MetricRule();
      metricRule.loadMetricRule(metricRuleArray);
    } catch (Exception e) {
      logger.error(e.getMessage());
      loggingRule = null;
    }
    
    //load ipfilter rule
    List<Map<String, String>> ipFilter = (List<Map<String, String>>) config.get("IPFilter");
    try {
      for (Map<String, String> child : ipFilter) {
        for(String item : child.get("List").split(",")){
          ipFilterRule.getHosts().add(item.trim());
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
      ipFilterRule = null;
    }
  }

  /*get, set*/
  public static RoutingRule getRoutingRule() {
    return routingRule;
  }

  public static void setRoutingRule(RoutingRule routingRule) {
    Configuration.routingRule = routingRule;
  }

  public static LoggingRule getLoggingRule() {
    return loggingRule;
  }

  public static void setLoggingRule(LoggingRule loggingRule) {
    Configuration.loggingRule = loggingRule;
  }

  public static MetricRule getMetricRule() {
    return metricRule;
  }

  public static void setMetricRule(MetricRule metricRule) {
    Configuration.metricRule = metricRule;
  }

  public static IpFilterRule getIpFilterRule() {
    return ipFilterRule;
  }

  public static void setIpFilterRule(IpFilterRule ipFilterRule) {
    Configuration.ipFilterRule = ipFilterRule;
  }

  public static String getHost() {
    return host;
  }

  public static void setHost(String host) {
    Configuration.host = host;
  }

  public static String getKeyspace() {
    return keyspace;
  }

  public static void setKeyspace(String keyspace) {
    Configuration.keyspace = keyspace;
  }
}
