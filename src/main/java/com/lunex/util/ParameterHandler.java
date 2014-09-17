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

  public static void getPropertiesValues() throws IOException {

    Properties prop = new Properties();
    String propFileName = "db.properties";

    InputStream inputStream = new FileInputStream(propFileName);
    prop.load(inputStream);
    DB_HOST = prop.getProperty("DB.HOST");
    DB_USERNAME = prop.getProperty("DB.USERNAME");
    DB_PASS = prop.getProperty("DB.PASS");
    DB_DBNAME = prop.getProperty("DB.DBNAME");
  }

}
