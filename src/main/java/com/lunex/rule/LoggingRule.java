package com.lunex.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lunex.util.LoggingRulePattern;

/**
 * List rule for logging
 * 
 * @author BaoLe
 *
 */
public class LoggingRule {
  private List<LoggingRulePattern> listRulePattern;

  public List<LoggingRulePattern> getListRulePattern() {
    return listRulePattern;
  }

  public void setListRulePattern(List<LoggingRulePattern> listRulePattern) {
    this.listRulePattern = listRulePattern;
  }

  public LoggingRule() {

  }

  public LoggingRule(List<LoggingRulePattern> rulePattern) {
    this.listRulePattern = rulePattern;
  }

  /**
   * Load rule for logging from config
   * 
   * @author BaoLe
   * @param listRule
   * @throws Exception
   */
  public void loadLoggingRule(List<Map<String, Object>> listRule) throws Exception {
    try {
      this.listRulePattern = new ArrayList<LoggingRulePattern>();
      LoggingRulePattern rule = null;
      for (int i = 0; i < listRule.size(); i++) {
        Map<String, Object> ruleMap = listRule.get(i);
        rule =
            new LoggingRulePattern((String) ruleMap.get("Regexp"), (String) ruleMap.get("Option"));
        listRulePattern.add(rule);
      }
    } catch (Exception ex) {
      throw ex;
    }
  }

  @Override
  public String toString() {
    return "listRulePattern: " + listRulePattern.toString();
  }
}
