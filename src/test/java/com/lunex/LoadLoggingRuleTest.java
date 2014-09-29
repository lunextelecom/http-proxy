package com.lunex;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.lunex.enums.ELoggingOption;
import com.lunex.enums.EVerb;
import com.lunex.util.Configuration;


public class LoadLoggingRuleTest {

  @BeforeClass
  public static void oneTimeSetUp() {
    // one-time initialization code
    System.out.println("@BeforeClass - oneTimeSetUp");
    Configuration.loadConfig("log4j.properties", "app.properties", "src/test/resources/configuration1.yaml");
  }
  
  @Test
  public void testLoadLoggingRule() {
    assertEquals(true, Configuration.getProxyRule()!=null && Configuration.getProxyRule().getRoutes().get(0).getLoggings()!=null);
  }

  @Test
  public void testSize() {
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(0).getLoggings().size()==1);
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(1).getLoggings().size()==3);
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(2).getLoggings().size()==2);
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(3).getLoggings().size()==1);
  }

  @Test
  public void testRegex() {
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(0).getLoggings().get(ELoggingOption.req) != null);
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(1).getLoggings().get(ELoggingOption.resp_body) != null);
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(2).getLoggings().get(ELoggingOption.req_body) != null);
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(3).getLoggings().get(ELoggingOption.off) != null);
  }
  
  @Test
  public void testVerd() {
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(0).getLoggings().get(ELoggingOption.req).getVerbs().contains(EVerb.ALL));
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(1).getLoggings().get(ELoggingOption.req_body).getVerbs().contains(EVerb.POST));
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(2).getLoggings().get(ELoggingOption.req_body).getVerbs().contains(EVerb.ALL));
  }
  
  
}
