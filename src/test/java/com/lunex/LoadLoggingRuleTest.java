package com.lunex;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.lunex.balancing.RoundRobinStrategy;
import com.lunex.rule.RoutingRulePattern;
import com.lunex.util.Configuration;
import com.lunex.util.Constants.EVerb;


public class LoadLoggingRuleTest {

  @BeforeClass
  public static void oneTimeSetUp() {
    // one-time initialization code
    System.out.println("@BeforeClass - oneTimeSetUp");
    Configuration.loadConfig("log4j.properties", "db.properties", "src/test/resources/configuration1.yaml");
  }
  
  @Test
  public void testLoadLoggingRule() {
    assertEquals(true, Configuration.getLoggingRule()!=null && Configuration.getLoggingRule().getListRulePattern()!=null);
  }

  @Test
  public void testSize() {
    assertEquals(true, Configuration.getLoggingRule().getListRulePattern().size()==3);
  }

  @Test
  public void testRegex() {
    assertEquals(true, Configuration.getLoggingRule().getListRulePattern().get(0).getRegexp().equals("/didv2/dids?."));
    assertEquals(true, Configuration.getLoggingRule().getListRulePattern().get(1).getRegexp().equals("/atsys/ws/json."));
    assertEquals(true, Configuration.getLoggingRule().getListRulePattern().get(2).getRegexp().equals("/health?."));
  }
  
  @Test
  public void testVerd() {
    assertEquals(true, Configuration.getLoggingRule().getListRulePattern().get(0).getVerb() == EVerb.GET);
    assertEquals(true, Configuration.getLoggingRule().getListRulePattern().get(1).getVerb() == EVerb.NONE);
    assertEquals(true, Configuration.getLoggingRule().getListRulePattern().get(2).getVerb() == EVerb.POST);
  }
  
  @Test
  public void testOption() {
    assertEquals(true, Configuration.getLoggingRule().getListRulePattern().get(0).getOptionsStr().equalsIgnoreCase("on"));
    assertEquals(true, Configuration.getLoggingRule().getListRulePattern().get(1).getOptionsStr().equalsIgnoreCase("request, request_header, request_body, response_body"));
    assertEquals(true, Configuration.getLoggingRule().getListRulePattern().get(2).getOptionsStr().equalsIgnoreCase("off"));
  }
  
}
