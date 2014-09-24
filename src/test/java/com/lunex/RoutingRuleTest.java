package com.lunex;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.lunex.balancing.RoundRobinStrategy;
import com.lunex.rule.RoutingRulePattern;
import com.lunex.util.Configuration;


public class RoutingRuleTest {

  @BeforeClass
  public static void oneTimeSetUp() {
    // one-time initialization code
    System.out.println("@BeforeClass - oneTimeSetUp");
    Configuration.loadConfig("log4j.properties", "db.properties", "src/test/resources/configuration1.yaml");
  }
  
  @Test
  public void testLoadRoutingRule() {
    assertEquals(true, Configuration.getRoutingRule()!=null && Configuration.getRoutingRule().getListRulePattern()!=null);
  }

  @Test
  public void testSize() {
    assertEquals(true, Configuration.getRoutingRule().getListRulePattern().size()==3);
  }

  @Test
  public void testRegex() {
    assertEquals(true, Configuration.getRoutingRule().getListRulePattern().get(0).getRegexp().equals("/didv2/dids?."));
    assertEquals(true, Configuration.getRoutingRule().getListRulePattern().get(1).getRegexp().equals("/atsys/ws/json."));
    assertEquals(true, Configuration.getRoutingRule().getListRulePattern().get(2).getRegexp().equals("/health?."));
  }
  
  @Test
  public void testStrategy() {
    assertEquals(true, Configuration.getRoutingRule().getListRulePattern().get(0).getBalancingStrategy() instanceof RoundRobinStrategy);
    assertEquals(true, Configuration.getRoutingRule().getListRulePattern().get(1).getBalancingStrategy() instanceof RoundRobinStrategy);
    assertEquals(true, Configuration.getRoutingRule().getListRulePattern().get(2).getBalancingStrategy() instanceof RoundRobinStrategy);
  }
  
  @Test
  public void testTarget() {
    assertEquals(true, Configuration.getRoutingRule().getListRulePattern().get(0).getBalancingStrategy().geTargetAddresses().size()==1);
    assertEquals(true, Configuration.getRoutingRule().getListRulePattern().get(1).getBalancingStrategy().geTargetAddresses().size()==2);
    assertEquals(true, Configuration.getRoutingRule().getListRulePattern().get(2).getBalancingStrategy().geTargetAddresses().size()==2);
  }
  
  @Test
  public void testRoundRobin() {
    RoutingRulePattern rule = Configuration.getRoutingRule().getListRulePattern().get(1);
    assertEquals(true, rule.getBalancingStrategy().selectTarget().toString().equals("10.9.9.61:8801"));
    assertEquals(true, rule.getBalancingStrategy().selectTarget().toString().equals("10.9.9.61:8802"));
    rule.getBalancingStrategy().geTargetAddresses().get(0).setAlive(false);
    assertEquals(true, rule.getBalancingStrategy().selectTarget().toString().equals("10.9.9.61:8802"));
    assertEquals(true, rule.getBalancingStrategy().selectTarget().toString().equals("10.9.9.61:8802"));
    
  }
  
}
