package com.lunex.httpproxy.test;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.lunex.httpproxy.rule.LoggingInfo.LoggingOption;
import com.lunex.httpproxy.rule.RouteInfo.Verb;
import com.lunex.httpproxy.util.Configuration;


public class LoadLoggingRuleTest {

  @BeforeClass
  public static void oneTimeSetUp() throws Exception {
    // one-time initialization code
    System.out.println("@BeforeClass - oneTimeSetUp");
    Configuration.loadConfig("conf/queue.properties", "conf/porxy.properties", "conf/configuration.properties");
  }
  
  @Test
  public void testLoadLoggingRule() {
    assertEquals(true, Configuration.getProxyRule()!=null && Configuration.getProxyRule().getRoutes().get(0).getLoggings()!=null);
  }

  @Test
  public void testSize() {
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(0).getLoggings().size()==1);
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(1).getLoggings().size()==2);
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(2).getLoggings().size()==3);
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(3).getLoggings().size()==1);
  }

  @Test
  public void testRegex() {
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(0).getLoggings().get(LoggingOption.req) != null);
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(1).getLoggings().get(LoggingOption.req_body) != null);
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(2).getLoggings().get(LoggingOption.resp_body) != null);
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(3).getLoggings().get(LoggingOption.off) != null);
  }
  
  @Test
  public void testVerd() {
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(0).getLoggings().get(LoggingOption.req).getVerbs().contains(Verb.ALL));
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(1).getLoggings().get(LoggingOption.req_body).getVerbs().contains(Verb.POST));
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(2).getLoggings().get(LoggingOption.resp_body).getVerbs().contains(Verb.POST));
  }
  
  
}
