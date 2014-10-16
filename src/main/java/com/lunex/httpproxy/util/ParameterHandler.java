package com.lunex.httpproxy.util;

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
  
  public static int HTTP_PROXY_SCHEDULE_TIME;

  /**
   * Gets the properties values.
   *
   * @param propFileName the prop file name
   * @throws IOException the IO exception
   */
  public static void getProxyProps(String propFileName) throws IOException {

    try {
      Properties prop = new Properties();

      InputStream inputStream = new FileInputStream(propFileName);
      prop.load(inputStream);
      HTTP_PROXY_PORT = Integer.valueOf(prop.getProperty("HTTP_PROXY.PORT").trim());
      HTTP_PROXY_ADMIN_PORT = Integer.valueOf(prop.getProperty("HTTP_PROXY.ADMIN_PORT").trim());
      HTTP_PROXY_NUM_THREAD = Integer.valueOf(prop.getProperty("HTTP_PROXY.NUM_THREAD").trim());
      HTTP_PROXY_SCHEDULE_TIME = Integer.valueOf(prop.getProperty("HTTP_PROXY.SCHEDULE_TIME").trim());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public static void getAppProps(String propFileName) throws IOException {

    try {
      Properties prop = new Properties();

      InputStream inputStream = new FileInputStream(propFileName);
      prop.load(inputStream);
      DB_HOST = prop.getProperty("DB.HOST").trim();
      DB_DBNAME = prop.getProperty("DB.DBNAME").trim();
      METRIC_HOST = prop.getProperty("METRIC.HOST").trim();
      METRIC_PORT = Integer.valueOf(prop.getProperty("METRIC.PORT").trim());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
