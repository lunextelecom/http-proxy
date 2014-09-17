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
public class LoggingRulePattern {
  private String regexp;
  private EVerb verb;
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

  public EVerb getVerb() {
    return verb;
  }

  public void setVerb(EVerb verb) {
    this.verb = verb;
  }

  public LoggingRulePattern() {

  }

  public LoggingRulePattern(String regexp, String verb, String optionsStr) {
    this.regexp = regexp;
    this.verb = EVerb.valueOf(verb.toUpperCase());
    this.optionsStr = optionsStr;
    this.options = Arrays.asList(optionsStr.split(","));
  }

  @Override
  public String toString() {
    return "regexp: " + regexp + ", verb: " + verb.toString() + ", options: " + options.toString();
  }
}
