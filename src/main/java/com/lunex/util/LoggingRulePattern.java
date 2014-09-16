package com.lunex.util;

import java.util.Arrays;
import java.util.List;

/**
 * Rule for Logging
 * 
 * @author BaoLe
 *
 */
public class LoggingRulePattern {
  private String regexp;
  private String optionsStr;
  private List<String> options;

  public String getRegexp() {
    return regexp;
  }

  public void setRegexp(String regexp) {
    this.regexp = regexp;
  }

  public String getOptionsStr() {
    return optionsStr;
  }

  public void setOptionsStr(String optionsStr) {
    this.optionsStr = optionsStr;
  }

  public List<String> getOptions() {
    return options;
  }

  public void setOptions(List<String> options) {
    this.options = options;
  }

  public LoggingRulePattern() {

  }

  public LoggingRulePattern(String regexp, String optionsStr) {
    this.regexp = regexp;
    this.optionsStr = optionsStr;
    this.options = Arrays.asList(optionsStr.split(","));
  }
  
  @Override
  public String toString() {
    return "regexp: " + regexp + ", options: " + options.toString();
  }
}
