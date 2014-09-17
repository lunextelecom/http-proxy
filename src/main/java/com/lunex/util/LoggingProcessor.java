package com.lunex.util;

import java.util.ArrayList;
import java.util.List;

import com.lunex.rule.LoggingRule;
import com.lunex.util.Constants.ELoggingOption;
import com.lunex.util.Constants.EVerb;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public class LoggingProcessor {

  public static void writeLog(HttpResponse response, HttpRequest request) {
    List<String> options = new ArrayList<String>();
    options.add(ELoggingOption.request.toString());
    options.add(ELoggingOption.request_header.toString());
    options.add(ELoggingOption.request_body.toString());
    options.add(ELoggingOption.response_body.toString());
  }

  public static void writeLog(HttpRequest request, LoggingRule loggingRule) {
    LoggingRulePattern pattern = loggingRule.selectRulePattern(request);
    List<String> options = pattern.getOptions();
    writeLog(request, options);
  }

  public static void writeLog(HttpResponse response, HttpRequest request, LoggingRule loggingRule) {
    LoggingRulePattern pattern = loggingRule.selectRulePattern(request);
    List<String> options = pattern.getOptions();
    writeLog(response, options);
  }

  public static void writeLog(Object obj, List<String> options) {
    if (options.contains(ELoggingOption.request)) {
      if (!(obj instanceof HttpRequest)) {
        return;
      }
      // TODO

    }
    if (options.contains(ELoggingOption.request_header)) {
      if (!(obj instanceof HttpRequest)) {
        return;
      }
      // TODO
    }
    if (options.contains(ELoggingOption.request_body)) {
      if (!(obj instanceof HttpRequest)) {
        return;
      }
      // TODO
    }
    if (options.contains(ELoggingOption.response_body)) {
      if (!(obj instanceof HttpResponse)) {
        return;
      }
      // TODO
    }

    if (options.contains(ELoggingOption.off)) {
      // TODO nothing with this rule
      return;
    }

    if (options.contains(ELoggingOption.on)) {
      if (!(obj instanceof HttpRequest)) {
        return;
      }
      // TODO
    }
  }
}
