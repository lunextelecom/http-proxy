package com.lunex.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ParameterHandler {

  public static String DB_HOST;
  public static String DB_USERNAME;
  public static String DB_PASS;
  public static String DB_DBNAME;
  
  public static String METRIC_HOST;
  public static int METRIC_PORT;
  public static int HTTP_PROXY_PORT;

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
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
