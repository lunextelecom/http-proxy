package com.lunex;

import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.BeforeClass;
import org.junit.Test;

import com.lunex.scheduler.JobScheduler;
import com.lunex.util.Configuration;
public class LoadIpFilterRuleTest {

  private final String USER_AGENT = "Mozilla/5.0";
  private final String AUTH = "admin";
  
  @BeforeClass
  public static void oneTimeSetUp() {
    // one-time initialization code
    System.out.println("@BeforeClass - oneTimeSetUp");
    Configuration.loadConfig("log4j.properties", "db.properties", "src/test/resources/configuration1.yaml");
    App.startHttpProxy();
    JobScheduler.run();
  }
  
  @Test
  public void testLoadIpFilterRule() {
    assertEquals(true, Configuration.getIpFilterRule()!=null && Configuration.getIpFilterRule().getHosts()!=null);
  }

  @Test
  public void testSize() {
    assertEquals(true, Configuration.getIpFilterRule()!=null && Configuration.getIpFilterRule().getHosts().size()==5);
  }

  @Test
  public void testAuthen() {
    
    
    String url = "http://127.0.0.1:8080/health";

    try {
      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("User-Agent", USER_AGENT);
//    con.setRequestProperty("Username", AUTH);
      con.setRequestProperty("Password", AUTH);
      int responseCode = con.getResponseCode();
      assertEquals(400, responseCode);
      
      con = (HttpURLConnection) obj.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("User-Agent", USER_AGENT);
      con.setRequestProperty("Username", AUTH);
      con.setRequestProperty("Password", AUTH);
      responseCode = con.getResponseCode();
      con.disconnect();
      assertEquals(200, responseCode);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testIpFilter() {
    
    
//    String url = "http://127.0.0.1:8080/health";
//
//    try {
//      URL obj = new URL(url);
//      HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//      con.setRequestMethod("GET");
//      con.setRequestProperty("User-Agent", USER_AGENT);
//      con.setRequestProperty("Username", AUTH);
//      con.setRequestProperty("Password", AUTH);
//      int responseCode = con.getResponseCode();
//      con.disconnect();
//      assertEquals(200, responseCode);
//      
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
  }
}
