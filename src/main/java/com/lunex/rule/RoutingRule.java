package com.lunex.rule;

import io.netty.handler.codec.http.HttpRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.lunex.util.Configuration;
import com.lunex.util.Constants;

/**
 * Class RoutingRule Contain list routing rule
 * 
 * @author BaoLe
 * @update DuyNguyen
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
        List<String> targetStrs = new ArrayList<>();
        for(String child : targetStr.split(",")){
          targetStrs.add(child.trim());
        }
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

  /**
   * Update rule pattern
   * 
   * @author BaoLe
   * @param rule
   */
  public void updateRulePattern(RoutingRulePattern rule) {
    if (rule == null || Constants.EMPTY_STRING.equals(rule.getRegexp()) || rule.getBalancingStrategy() == null) {
      return;
    }
    for (int i = 0; i < listRulePattern.size(); i++) {
      if (rule.getRegexp().equals(listRulePattern.get(i).getRegexp())) {
        listRulePattern.set(i, rule);
        break;
      }
    }
  }
  
  /**
   * Get rule pattern from http request
   * 
   * @author BaoLe
   * @update DuyNguyen
   * @param request
   * @return
   */
  public RoutingRulePattern selectRulePattern(String url) {
    for (RoutingRulePattern child : listRulePattern) {
      if(child.getPattern() != null){
        Matcher m = child.getPattern().matcher(url);
        if (m.find()){
          return child;
        }
      }
    }
    return null;
  }
  
  @Override
  public String toString() {
    return "listRulePattern: " + this.listRulePattern.toString();
  }
  
}
