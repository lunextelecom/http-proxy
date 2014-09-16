package com.lunex.util;

import java.util.ArrayList;
import java.util.List;

import com.lunex.balancing.BalancingStrategy;
import com.lunex.balancing.RoundRobinBalancingStrategy;
import com.lunex.util.Constants.EBalancingStrategy;

public class RulePattern {

  private String regexp;

  private EBalancingStrategy balancer;

  private BalancingStrategy balancingStrategy;

  public RulePattern() {

  }

  public RulePattern(String regexp, EBalancingStrategy balancer) {
    this.regexp = regexp;
    this.balancer = balancer;
  }

  public void createBalancingStrategy(List<String> targetStrs) {
    List<HostAndPort> targets = new ArrayList<HostAndPort>();
    String targetStr = null;
    for (int i = 0; i < targetStrs.size(); i++) {
      targetStr = targetStrs.get(i);
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

  public BalancingStrategy getBalancingStrategy() {
    return balancingStrategy;
  }

  public void setBalancingStrategy(BalancingStrategy balancingStrategy) {
    this.balancingStrategy = balancingStrategy;
  }

  @Override
  public String toString() {
    return "regexp: " + regexp + ", balancer: " + balancer.toString() + ", balancingStrategy: "
        + balancingStrategy.toString();
  }
}
