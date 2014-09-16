package com.lunex.rule;

import java.util.ArrayList;
import java.util.List;

import com.lunex.util.RulePattern;

public class RoutingRule {
  private List<RulePattern> listRulePattern;
  
  public List<RulePattern> getListRulePattern() {
    return listRulePattern;
  }

  public void setListRulePattern(List<RulePattern> listRulePattern) {
    this.listRulePattern = listRulePattern;
  }

  public RoutingRule(List<RulePattern> rulePattern) {
    this.listRulePattern = rulePattern;
  }
  
  public void loadRoutingRule(String configFile) {
    listRulePattern = new ArrayList<RulePattern>();

    RulePattern rule = new RulePattern();
    for (int i = 0; i < 10; i++) {
      listRulePattern.add(rule);
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
}
