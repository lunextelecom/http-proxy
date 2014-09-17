package com.lunex.rule;

import com.lunex.util.Constants.EVerb;

/**
 * Rule for metric
 * 
 * @author BaoLe
 *
 */
public class MetricRulePattern extends LoggingRulePatternAbtract {

  private String metric;

  public String getMetric() {
    return metric;
  }

  public void setMetric(String metric) {
    this.metric = metric;
  }

  public MetricRulePattern() {

  }

  public MetricRulePattern(String regexp, String verb, String metric) {
    this.regexp = regexp;
    this.verb = EVerb.valueOf(verb.toUpperCase());
    this.metric = metric;
  }

  @Override
  public String toString() {
    return "regexp: " + regexp + ", verb: " + verb.toString() + ", metric: " + metric.toString();
  }
}
