package com.lunex.rule;

import com.lunex.util.Constants.EVerb;

public class LoggingRulePatternAbtract {
  protected String regexp;
  protected EVerb verb;

  public String getRegexp() {
    return regexp;
  }

  public void setRegexp(String regexp) {
    this.regexp = regexp;
  }

  public EVerb getVerb() {
    return verb;
  }

  public void setVerb(EVerb verb) {
    this.verb = verb;
  }
}
