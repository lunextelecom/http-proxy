package com.lunex.httpproxy.test;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.lunex.httpproxy.balancing.LeastUseStrategy;
import com.lunex.httpproxy.balancing.RoundRobinStrategy;
import com.lunex.httpproxy.rule.ServerInfo;
import com.lunex.httpproxy.util.Configuration;


public class RoutingRuleTest {

  @BeforeClass
  public static void oneTimeSetUp() throws Exception {
    // one-time initialization code
    System.out.println("@BeforeClass - oneTimeSetUp");
    Configuration.loadConfig("conf/queue.properties", "conf/porxy.properties", "conf/configuration.properties");
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
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(1).getPattern().matcher("/health").find());
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(2).getPattern().matcher("/home").find());
    assertEquals(true, Configuration.getProxyRule().getRoutes().get(3).getPattern().matcher("/authsvc.*").find());
  }
  
  @Test
  public void testStrategy() {
    assertEquals(true, Configuration.getProxyRule().getServers().get("health_server").getBalancingStrategy() instanceof RoundRobinStrategy);
    assertEquals(true, Configuration.getProxyRule().getServers().get("dummy_server").getBalancingStrategy() instanceof RoundRobinStrategy);
    assertEquals(true, Configuration.getProxyRule().getServers().get("authen_server").getBalancingStrategy() instanceof LeastUseStrategy);
  }
  
  @Test
  public void testTarget() {
    assertEquals(true, Configuration.getProxyRule().getServers().get("health_server").getTargets().size()==1);
    assertEquals(true, Configuration.getProxyRule().getServers().get("dummy_server").getTargets().size()==2);
    assertEquals(true, Configuration.getProxyRule().getServers().get("authen_server").getTargets().size()==3);
  }
  
  @Test
  public void testRoundRobin() {
    ServerInfo server = Configuration.getProxyRule().getServers().get("dummy_server");
    assertEquals(true, server.getBalancingStrategy().selectTarget(server.getTargets()).toString().equals("192.168.93.61:9090/dummyws"));
    assertEquals(true, server.getBalancingStrategy().selectTarget(server.getTargets()).toString().equals("192.168.93.62:9090/dummyws"));
    server.getTargets().get(0).setAlive(false);
    assertEquals(true, server.getBalancingStrategy().selectTarget(server.getTargets()).toString().equals("192.168.93.62:9090/dummyws"));
    assertEquals(true, server.getBalancingStrategy().selectTarget(server.getTargets()).toString().equals("192.168.93.62:9090/dummyws"));
    
  }
  
  
}
