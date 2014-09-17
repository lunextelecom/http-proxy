package com.lunex.util;

import java.util.List;

import com.lunex.rule.LoggingRule;
import com.lunex.util.Constants.ELoggingOption;


public class LoggingProcessor {

  public static void writeLog(LogObject logObject, LoggingRule loggingRule) {
    LoggingRulePattern pattern = loggingRule.selectRulePattern(logObject.getRequest());
    List<String> options = pattern.getOptions();
    writeLog(logObject, options);
  }

  public static void writeLog(LogObject obj, List<String> options) {
    if (options.contains(ELoggingOption.off)) {
      // TODO nothing with this rule
      return;
    }

    if (options.contains(ELoggingOption.on)) {
      if (obj.getRequest() != null) {
        // TODO
        return;
      }
    }

    if (options.contains(ELoggingOption.request)) {
      if (obj.getRequest() != null) {
        // TODO
      }

    }
    if (options.contains(ELoggingOption.request_header)) {
      if (obj.getRequestHeaders() != null) {
        // TODO
      }
    }
    if (options.contains(ELoggingOption.request_body)) {
      if (obj.getRequestContent() != null) {
        // TODO
      }
    }
    if (options.contains(ELoggingOption.response_body)) {
      if (obj.getResponseContent() != null) {
        // TODO
      }
    }

  }
}
