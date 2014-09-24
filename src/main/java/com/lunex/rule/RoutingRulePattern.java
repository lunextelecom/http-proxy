package com.lunex.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.lunex.balancing.IBalancingStrategy;
import com.lunex.balancing.RoundRobinStrategy;
import com.lunex.util.Constants.EBalancingStrategy;
import com.lunex.util.HostAndPort;

/**
 * Rule for Routing
 * @author BaoLe
 *
 */
public class RoutingRulePattern {

  private String regexp;

  private EBalancingStrategy balancer;

  private IBalancingStrategy balancingStrategy;

  private Pattern pattern = null;
  
  public RoutingRulePattern() {
  }

  public RoutingRulePattern(String regexp, EBalancingStrategy balancer) {
    this.regexp = regexp;
    this.balancer = balancer;
    this.pattern = Pattern.compile(regexp);
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
        balancingStrategy = new RoundRobinStrategy(targets);
        break;
      default:
        break;
    }
  }

  @Override
  public String toString() {
    return "regexp: " + regexp + ", balancer: " + balancer.toString() + ", balancingStrategy: "
        + balancingStrategy.toString();
  }
  
  /*get, set*/
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
  
  public Pattern getPattern() {
    return pattern;
  }

  public void setPattern(Pattern pattern) {
    this.pattern = pattern;
  }
  
}
