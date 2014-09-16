package com.lunex.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.lunex.util.Constants.EBalancingStrategy;

/**
 * Configuration of System
 * 
 * @author BaoLe
 *
 */
public class Configuration {

  public static Map<String, EBalancingStrategy> MAP_BALANCER_STATEGY;

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

}
