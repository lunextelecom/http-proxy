package com.lunex.rule;

import java.util.ArrayList;
import java.util.List;

import com.lunex.balancing.IBalancingStrategy;
import com.lunex.balancing.RoundRobinBalancingStrategy;
import com.lunex.util.HostAndPort;
import com.lunex.util.Constants.EBalancingStrategy;

/**
 * Rule for Routing
 * @author BaoLe
 *
 */
public class RoutingRulePattern {

  private String regexp;

  private EBalancingStrategy balancer;

  private IBalancingStrategy balancingStrategy;

  public RoutingRulePattern() {

  }

  public RoutingRulePattern(String regexp, EBalancingStrategy balancer) {
    this.regexp = regexp;
    this.balancer = balancer;
  }

  public void createBalancingStrategy(List<String> targetStrs) {
    List<HostAndPort> targets = new ArrayList<HostAndPort>();
    String targetStr = null;
    for (int i = 0; i < targetStrs.size(); i++) {
      targetStr = targetStrs.get(i).trim();
      String[] array = targetStr.split(":");
      targets.add(new HostAndPort(array[0], Integer.valueOf(array[1])));
    }
    switch (this.balancer) {
      case ROUND_ROBIN:
        balancingStrategy = new RoundRobinBalancingStrategy(targets);
        break;
      default:
        break;
    }
  }

  public String getRegexp() {
    return regexp;
  }

  public void setRegexp(String regexp) {
    this.regexp = regexp;
  }

  public IBalancingStrategy getBalancingStrategy() {
    return balancingStrategy;
  }

  public void setBalancingStrategy(IBalancingStrategy balancingStrategy) {
    this.balancingStrategy = balancingStrategy;
  }

  @Override
  public String toString() {
    return "regexp: " + regexp + ", balancer: " + balancer.toString() + ", balancingStrategy: "
        + balancingStrategy.toString();
  }
}
