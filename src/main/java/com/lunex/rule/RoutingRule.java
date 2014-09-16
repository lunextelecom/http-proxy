package com.lunex.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lunex.util.Configuration;
import com.lunex.util.RulePattern;

public class RoutingRule {
  private List<RulePattern> listRulePattern;

  public List<RulePattern> getListRulePattern() {
    return listRulePattern;
  }

  public void setListRulePattern(List<RulePattern> listRulePattern) {
    this.listRulePattern = listRulePattern;
  }

  public RoutingRule() {

  }

  public RoutingRule(List<RulePattern> rulePattern) {
    this.listRulePattern = rulePattern;
  }

  public void loadRoutingRule(List<Map<String, Object>> listRule) throws Exception {
    try {
      listRulePattern = new ArrayList<RulePattern>();
      RulePattern rule = null;
      for (int i = 0; i < listRule.size(); i++) {
        Map<String, Object> ruleMap = listRule.get(i);
        rule =
            new RulePattern(ruleMap.get("Regexp").toString(),
                Configuration.MAP_BALANCER_STATEGY.get(ruleMap.get("Balancer").toString()));
        String targetStr = (String) ruleMap.get("Target");
        String[] arrayTarget = targetStr.split(",");
        List<String> targetStrs = (Arrays.asList(arrayTarget));
        rule.createBalancingStrategy(targetStrs);
        listRulePattern.add(rule);
      }
    } catch (Exception e) {
      throw e;
    }
  }

  public void deleteRulePattern(RulePattern rule) {
    for (int i = 0; i < listRulePattern.size(); i++) {
      if (rule.getRegexp() == listRulePattern.get(i).getRegexp()) {
        listRulePattern.remove(i);
        break;
      }
    }
  }

  public void deleteRulePattern(String regexp) {
    for (int i = 0; i < listRulePattern.size(); i++) {
      if (regexp == listRulePattern.get(i).getRegexp()) {
        listRulePattern.remove(i);
        break;
      }
    }
  }

  public void addRulePattern(RulePattern rule) {
    this.listRulePattern.add(rule);
  }

  @Override
  public String toString() {
    return "listRulePattern: " + this.listRulePattern.toString();
  }
}
