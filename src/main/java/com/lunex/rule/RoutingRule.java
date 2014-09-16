package com.lunex.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.lunex.util.Configuration;
import com.lunex.util.RoutingRulePattern;

/**
 * Class RoutingRule Contain list routing rule
 * 
 * @author BaoLe
 *
 */
public class RoutingRule {
  private List<RoutingRulePattern> listRulePattern;

  public List<RoutingRulePattern> getListRulePattern() {
    return listRulePattern;
  }

  public void setListRulePattern(List<RoutingRulePattern> listRulePattern) {
    this.listRulePattern = listRulePattern;
  }

  public RoutingRule() {

  }

  public RoutingRule(List<RoutingRulePattern> rulePattern) {
    this.listRulePattern = rulePattern;
  }

  /**
   * Load list routing rule pattern from list rule get from config
   * 
   * @author BaoLe
   * @param listRule
   * @throws Exception
   */
  public void loadRoutingRule(List<Map<String, Object>> listRule) throws Exception {
    try {
      listRulePattern = new ArrayList<RoutingRulePattern>();
      RoutingRulePattern rule = null;
      for (int i = 0; i < listRule.size(); i++) {
        Map<String, Object> ruleMap = listRule.get(i);
        rule =
            new RoutingRulePattern(ruleMap.get("Regexp").toString(),
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

  /**
   * Delete rule pattern from list by rule
   * 
   * @author BaoLe
   * @param rule
   */
  public void deleteRulePattern(RoutingRulePattern rule) {
    for (int i = 0; i < listRulePattern.size(); i++) {
      if (rule.getRegexp() == listRulePattern.get(i).getRegexp()) {
        listRulePattern.remove(i);
        break;
      }
    }
  }

  /**
   * Delete rule pattern from list by regular expression
   * 
   * @author BaoLe
   * @param regexp
   */
  public void deleteRulePattern(String regexp) {
    for (int i = 0; i < listRulePattern.size(); i++) {
      if (regexp == listRulePattern.get(i).getRegexp()) {
        listRulePattern.remove(i);
        break;
      }
    }
  }

  /**
   * Add new rule pattern
   * 
   * @author BaoLe
   * @param rule
   */
  public void addRulePattern(RoutingRulePattern rule) {
    this.listRulePattern.add(rule);
  }

  @Override
  public String toString() {
    return "listRulePattern: " + this.listRulePattern.toString();
  }
}
