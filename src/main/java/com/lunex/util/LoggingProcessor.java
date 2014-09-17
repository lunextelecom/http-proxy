package com.lunex.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.App;
import com.lunex.rule.LoggingRule;
import com.lunex.util.Constants.ELoggingOption;


public class LoggingProcessor {

  static final Logger logger = LoggerFactory.getLogger(LoggingProcessor.class);

  /**
   * Write log with logging config
   * 
   * @author BaoLe
   * @param logObject
   * @param loggingRule
   */
  public static void writeLog(LogObject logObject, LoggingRule loggingRule) {
    if (logObject == null) {
      return;
    }
    LoggingRulePattern pattern = loggingRule.selectRulePattern(logObject.getRequest());
    if (pattern != null) {
      List<String> options = pattern.getOptions();
      writeLog(logObject, options);
    } else {
      writeLog(logObject);
    }
  }

  /**
   * Write log to DB
   * 
   * @author BaoLe
   * @param logObject
   */
  public static void writeLog(LogObject logObject) {
    String sql =
        "insert into logging(id, client, target, method, request, request_header, request_body, response_body)";
    sql += "values (now(), ?, ?, ?, ?, ?, ?, ?);";
    List<Object> listParams = new ArrayList<Object>();
    listParams.add(logObject.getClient());
    listParams.add(logObject.getTarget());
    listParams.add(logObject.getMethod().toString());
    listParams.add(logObject.getRequest() == null ? null : logObject.getRequest().getUri());
    listParams.add(logObject.getRequestHeaders() == null ? null : logObject.getRequestHeaders().toString());
    listParams.add(logObject.getRequestContent() == null ? null : logObject.getRequestContent().toString());
    listParams.add(logObject.getResponseContent() == null ? null : logObject.getResponseContent().toString());
    try {
      App.dbResource.executeQueryWithParamsNonQuery(sql, listParams);

    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * 
   * @param logObject
   * @param logginOptions
   */
  public static void writeLog(LogObject logObject, List<String> logginOptions) {
    if (logginOptions.contains(ELoggingOption.off.toString())) {
      // TODO nothing with this rule
      return;
    }

    if (logginOptions.contains(ELoggingOption.on.toString())) {
      if (logObject.getRequest() != null) {
        // TODO
        // only log request
        logObject.setRequestHeaders(null);
        logObject.setRequestContent(null);
        logObject.setResponseContent(null);
        writeLog(logObject);
        return;
      }
    } else {
      if (logginOptions.contains(ELoggingOption.request.toString())) {
        if (logObject.getRequest() == null) {
          logger.error("Can not log this request with request object is null");
          return;
        }
      } else {
        logObject.setRequest(null);
      }
      if (logginOptions.contains(ELoggingOption.request_header.toString())) {
        if (logObject.getRequestHeaders() == null) {
          logger.error("Can not log this request with request header object is null");
          return;
        }
      } else {
        logObject.setRequestHeaders(null);
      }
      if (logginOptions.contains(ELoggingOption.request_body.toString())) {
        if (logObject.getRequestContent() == null) {
          logger.error("Can not log this request with request body content object is null");
          return;
        }
      } else {
        logObject.setRequestContent(null);
      }
      if (logginOptions.contains(ELoggingOption.response_body.toString())) {
        if (logObject.getResponseContent() == null) {
          logger.error("Can not log this request with response body content object is null");
          return;
        }
      } else {
        logObject.setResponseContent(null);
      }
      writeLog(logObject);
    }
  }
}
