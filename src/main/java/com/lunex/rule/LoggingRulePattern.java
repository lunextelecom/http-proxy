package com.lunex.rule;

import java.util.Arrays;
import java.util.List;

import com.lunex.util.Constants;
import com.lunex.util.Constants.EVerb;

/**
 * Rule for Logging
 * 
 * @author BaoLe
 *
 */
public class LoggingRulePattern extends LoggingRulePatternAbtract {

  private String optionsStr;
  private List<String> options;



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

  public LoggingRulePattern(String regexp, String verb, String optionsStr) {
    this.regexp = regexp;
    this.verb = EVerb.valueOf(verb.toUpperCase());
    this.optionsStr = optionsStr;
    this.options = Arrays.asList(optionsStr.replaceAll("\\s+","").split(","));
  }

  @Override
  public String toString() {
    return "regexp: " + regexp + ", verb: " + verb.toString() + ", options: " + options.toString();
  }
}
