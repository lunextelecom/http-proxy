package com.lunex.logging;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.cassandra.CassandraRepository;
import com.lunex.rule.LoggingRule;
import com.lunex.rule.LoggingRulePattern;
import com.lunex.util.Constants.ELoggingOption;
import com.lunex.util.LogObject;


/**
 * Process write log with logging configuration
 * 
 * @author BaoLe
 * @udpate DuyNguyen
 */
public class LoggingProcessor {

  static final Logger logger = LoggerFactory.getLogger(LoggingProcessor.class);

  /**
   * Write log with logging config
   * 
   * @author BaoLe
   * @param logObject
   * @param loggingRule
   */
  public static void writeLogging(LogObject logObject, LoggingRule loggingRule) {
    if (logObject == null) {
      return;
    }
    LoggingRulePattern pattern = loggingRule.selectRulePattern(logObject.getRequest(), logObject.getMethod().toString());
    if (pattern != null) {
      List<String> options = pattern.getOptions();
      writeLogging(logObject, options);
    } else {
      writeLogging(logObject);
    }
  }

  /**
   * Write log to DB
   * 
   * @author BaoLe
   * @param logObject
   */
  private static void writeLogging(LogObject logObject) {
    CassandraRepository.getInstance().insertLogging(logObject);
  }

  /**
   * 
   * @param logObject
   * @param logginOptions
   */
  private static void writeLogging(LogObject logObject, List<String> logginOptions) {
    if (logginOptions.contains(ELoggingOption.off.toString())) {
      return;
    }

    if (logginOptions.contains(ELoggingOption.on.toString())) {
      if (logObject.getRequest() != null) {
        // only log request
        logObject.setRequestHeaders(null);
        logObject.setRequestContent(null);
        logObject.setResponseContent(null);
        writeLogging(logObject);
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
      writeLogging(logObject);
    }
  }
}
