package com.lunex;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.lunex.balancing.RoundRobinStrategy;
import com.lunex.rule.RoutingRulePattern;
import com.lunex.util.Configuration;
import com.lunex.util.Constants.EVerb;


public class LoadMetricRuleTest {

  @BeforeClass
  public static void oneTimeSetUp() {
    // one-time initialization code
    System.out.println("@BeforeClass - oneTimeSetUp");
    Configuration.loadConfig("log4j.properties", "db.properties", "src/test/resources/configuration1.yaml");
  }
  
  @Test
  public void testLoadMetricRule() {
    assertEquals(true, Configuration.getMetricRule()!=null && Configuration.getMetricRule().getListRulePattern()!=null);
  }

  @Test
  public void testSize() {
    assertEquals(true, Configuration.getMetricRule().getListRulePattern().size()==3);
  }

  @Test
  public void testRegex() {
    assertEquals(true, Configuration.getMetricRule().getListRulePattern().get(0).getRegexp().equals("/didv2/dids?."));
    assertEquals(true, Configuration.getMetricRule().getListRulePattern().get(1).getRegexp().equals("/atsys/ws/json."));
    assertEquals(true, Configuration.getMetricRule().getListRulePattern().get(2).getRegexp().equals("/health?."));
  }
  
  @Test
  public void testVerd() {
    assertEquals(true, Configuration.getMetricRule().getListRulePattern().get(0).getVerb() == EVerb.GET);
    assertEquals(true, Configuration.getMetricRule().getListRulePattern().get(1).getVerb() == EVerb.DELETE);
    assertEquals(true, Configuration.getMetricRule().getListRulePattern().get(2).getVerb() == EVerb.POST);
  }
  
  @Test
  public void testOption() {
    assertEquals(true, Configuration.getMetricRule().getListRulePattern().get(0).getMetric().equalsIgnoreCase("test.http.proxy.target.get_request"));
    assertEquals(true, Configuration.getMetricRule().getListRulePattern().get(1).getMetric().equalsIgnoreCase("test.http.proxy.target.get_request_ats"));
    assertEquals(true, Configuration.getMetricRule().getListRulePattern().get(2).getMetric().equalsIgnoreCase("test.http.proxy.target.get_request_health"));
  }
  
}
