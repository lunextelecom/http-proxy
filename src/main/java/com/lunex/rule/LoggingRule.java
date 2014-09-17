package com.lunex.rule;

import io.netty.handler.codec.http.HttpRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lunex.App;
import com.lunex.util.Constants;
import com.lunex.util.Constants.EVerb;
import com.lunex.util.LoggingRulePattern;
import com.lunex.util.RoutingRulePattern;

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
            new LoggingRulePattern((String) ruleMap.get("Regexp"), 
                (String) ruleMap.get("Verb"),
                (String) ruleMap.get("Option"));
        listRulePattern.add(rule);
      }
    } catch (Exception ex) {
      throw ex;
    }
  }

  /**
   * Delete rule pattern by object
   * 
   * @author BaoLe
   * @param rule
   */
  public void deleteRulePattern(LoggingRulePattern rule) {
    if (rule == null || Constants.EMPTY_STRING.equals(rule.getRegexp())) {
      return;
    }
    for (int i = 0; i < listRulePattern.size(); i++) {
      if (rule.getRegexp().equals(listRulePattern.get(i).getRegexp())) {
        listRulePattern.remove(i);
        break;
      }
    }
  }

  /**
   * Delete rule pattern by regular expression string
   * 
   * @author BaoLe
   * @param regexp
   */
  public void deleteRulePattern(String regexp) {
    if (regexp == null || Constants.EMPTY_STRING.equals(regexp)) {
      return;
    }
    for (int i = 0; i < listRulePattern.size(); i++) {
      if (regexp.equals(listRulePattern.get(i).getRegexp())) {
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
  public void addRulePattern(LoggingRulePattern rule) {
    this.listRulePattern.add(rule);
  }

  /**
   * Update rule pattern
   * 
   * @author BaoLe
   * @param rule
   */
  public void updateRulePattern(LoggingRulePattern rule) {
    if (rule == null || Constants.EMPTY_STRING.equals(rule.getRegexp())) {
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
   * @param request
   * @return
   */
  public LoggingRulePattern selectRulePattern(HttpRequest request) {
    for (int i = 0; i < listRulePattern.size(); i++) {
      LoggingRulePattern rule = listRulePattern.get(i);
      Pattern r = Pattern.compile(rule.getRegexp());
      Matcher m = r.matcher(request.getUri());
      if (m.find())
        if (rule.getVerb() != EVerb.NONE) {
          if (rule.getVerb().equals(request.getMethod().toString())) {
            return rule;
          }
        }
        else {
          return rule;
        }
    }
    return null;
  }

  @Override
  public String toString() {
    return "listRulePattern: " + listRulePattern.toString();
  }
}
