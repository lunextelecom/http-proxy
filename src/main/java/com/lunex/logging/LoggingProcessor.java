package com.lunex.logging;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.cassandra.CassandraRepository;
import com.lunex.enums.ELoggingOption;
import com.lunex.rule.LoggingInfo;
import com.lunex.rule.RouteInfo;
import com.lunex.util.Configuration;
import com.lunex.util.LogObject;


/**
 * Process write log with logging configuration
 * 
 */
public class LoggingProcessor {

  static final Logger logger = LoggerFactory.getLogger(LoggingProcessor.class);

  /**
   * Write log with logging config
   * 
   * @param logObject
   * @param loggingRule
   */
  public static void writeLogging(String method, LogObject logObject, RouteInfo route) {
    if (logObject == null) {
      return;
    }
    if(Configuration.getProxyRule().isOffLogging(route)){
      return;
    }
    writeLogging(method, logObject, route.getLoggings());
  }

  /**
   * Write log to DB
   * 
   * @param logObject
   */
  private static void writeLogging(LogObject logObject) {
    try {
      CassandraRepository.getInstance().insertLogging(logObject);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * 
   * @param logObject
   * @param logginOptions
   */
  private static void writeLogging(String method, LogObject logObject, Map<ELoggingOption, LoggingInfo> loggings) {

    if (loggings.containsKey(ELoggingOption.req) && loggings.get(ELoggingOption.req).isWriteLogging(ELoggingOption.req, method)) {
      if (logObject.getRequest() == null) {
        logger.error("Can not log this request with request object is null");
        return;
      }
    } else {
      logObject.setRequest(null);
    }
    if (loggings.containsKey(ELoggingOption.req_header) && loggings.get(ELoggingOption.req_header).isWriteLogging(ELoggingOption.req_header, method)) {
      if (logObject.getRequestHeaders() == null) {
        logger.error("Can not log this request with request header object is null");
        return;
      }
    } else {
      logObject.setRequestHeaders(null);
    }
    if (loggings.containsKey(ELoggingOption.req_body) && loggings.get(ELoggingOption.req_body).isWriteLogging(ELoggingOption.req_body, method)) {
      if (logObject.getRequestContent() == null) {
        logger.error("Can not log this request with request body content object is null");
        return;
      }
    } else {
      logObject.setRequestContent(null);
    }
    if (loggings.containsKey(ELoggingOption.resp_body) && loggings.get(ELoggingOption.resp_body).isWriteLogging(ELoggingOption.resp_body, method)) {
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
