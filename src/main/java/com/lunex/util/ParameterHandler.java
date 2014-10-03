package com.lunex.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// TODO: Auto-generated Javadoc
/**
 * The Class ParameterHandler.
 */
public class ParameterHandler {

  /** The db host. */
  public static String DB_HOST;
  
  /** The db username. */
  public static String DB_USERNAME;
  
  /** The db pass. */
  public static String DB_PASS;
  
  /** The db dbname. */
  public static String DB_DBNAME;
  
  /** The metric host. */
  public static String METRIC_HOST;
  
  /** The metric port. */
  public static int METRIC_PORT;
  
  /** The http proxy port. */
  public static int HTTP_PROXY_PORT;
  
  public static int HTTP_PROXY_ADMIN_PORT;
  
  /** The http proxy num thread. */
  public static int HTTP_PROXY_NUM_THREAD;
  
  /** The http proxy config name. */
  public static String HTTP_PROXY_CONFIG_NAME;

  /**
   * Gets the properties values.
   *
   * @param propFileName the prop file name
   * @throws IOException the IO exception
   */
  public static void getPropertiesValues(String propFileName) throws IOException {

    try {
      Properties prop = new Properties();

      InputStream inputStream = new FileInputStream(propFileName);
      prop.load(inputStream);
      DB_HOST = prop.getProperty("DB.HOST");
      DB_USERNAME = prop.getProperty("DB.USERNAME");
      DB_PASS = prop.getProperty("DB.PASS");
      DB_DBNAME = prop.getProperty("DB.DBNAME");
      
      METRIC_HOST = prop.getProperty("METRIC.HOST");
      METRIC_PORT = Integer.valueOf(prop.getProperty("METRIC.PORT"));
      
      HTTP_PROXY_PORT = Integer.valueOf(prop.getProperty("HTTP_PROXY.PORT"));
      HTTP_PROXY_ADMIN_PORT = Integer.valueOf(prop.getProperty("HTTP_PROXY.ADMIN_PORT"));
      HTTP_PROXY_NUM_THREAD = Integer.valueOf(prop.getProperty("HTTP_PROXY.NUM_THREAD"));
      HTTP_PROXY_CONFIG_NAME = prop.getProperty("HTTP_PROXY.CONFIG_NAME");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
