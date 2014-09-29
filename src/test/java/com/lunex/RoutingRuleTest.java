package com.lunex;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.lunex.balancing.RoundRobinStrategy;
import com.lunex.rule.ServerInfo;
import com.lunex.util.Configuration;


public class RoutingRuleTest {

  @BeforeClass
  public static void oneTimeSetUp() {
    // one-time initialization code
    System.out.println("@BeforeClass - oneTimeSetUp");
    Configuration.loadConfig("log4j.properties", "app.properties", "src/test/resources/configuration1.yaml");
  }
  
  @Test
  public void testLoadRoutingRule() {
    assertEquals(true, Configuration.getProxyRule()!=null && Configuration.getProxyRule().getRoutes()!=null);
  }

  @Test
  public void testSize() {
    assertEquals(true, Configuration.getProxyRule().getRoutes().size()==4);
  }

  @Test
  public void testRegex() {
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(0).getPattern().matcher("/list").find());
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(1).getPattern().matcher("/pos/api/orders/id=1").find());
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(2).getPattern().matcher("/product/p=1").find());
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(2).getPattern().matcher("/products/p=1").find());
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(2).getPattern().matcher("/sku/p=1").find());
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(2).getPattern().matcher("/skus/p=1").find());
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(3).getPattern().matcher("/blabla/everything").find());
  }
  
  @Test
  public void testStrategy() {
    assertEquals(true, Configuration.getProxyRule().getServers().get("health_server").getBalancingStrategy() instanceof RoundRobinStrategy);
    assertEquals(true, Configuration.getProxyRule().getServers().get("pos_server").getBalancingStrategy() instanceof RoundRobinStrategy);
    assertEquals(true, Configuration.getProxyRule().getServers().get("catalog").getBalancingStrategy() instanceof RoundRobinStrategy);
    assertEquals(true, Configuration.getProxyRule().getServers().get("default_server").getBalancingStrategy() instanceof RoundRobinStrategy);
  }
  
  @Test
  public void testTarget() {
    assertEquals(true, Configuration.getProxyRule().getServers().get("health_server").getTargets().size()==1);
    assertEquals(true, Configuration.getProxyRule().getServers().get("pos_server").getTargets().size()==2);
    assertEquals(true, Configuration.getProxyRule().getServers().get("catalog").getTargets().size()==2);
    assertEquals(true, Configuration.getProxyRule().getServers().get("default_server").getTargets().size()==1);
  }
  
  @Test
  public void testRoundRobin() {
    ServerInfo server = Configuration.getProxyRule().getServers().get("pos_server");
    assertEquals(true, server.getBalancingStrategy().selectTarget(server.getTargets()).toString().equals("192.168.93.100:9090"));
    assertEquals(true, server.getBalancingStrategy().selectTarget(server.getTargets()).toString().equals("192.168.93.101:9090"));
    server.getTargets().get(0).setAlive(false);
    assertEquals(true, server.getBalancingStrategy().selectTarget(server.getTargets()).toString().equals("192.168.93.101:9090"));
    assertEquals(true, server.getBalancingStrategy().selectTarget(server.getTargets()).toString().equals("192.168.93.101:9090"));
    
  }
  
  
}
