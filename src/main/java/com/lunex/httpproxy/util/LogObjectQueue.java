package com.lunex.httpproxy.util;

import java.io.Serializable;

import com.lunex.httpproxy.rule.RouteInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class LogObjectQueue.
 */
public class LogObjectQueue implements Serializable {

  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 835531349906551492L;

  /** The log object. */
  private LogObject logObject;
  
  /** The selected route. */
  private RouteInfo selectedRoute;
  
  /** The method name. */
  private String methodName;
  
  /**
   * Gets the log object.
   *
   * @return the log object
   */
  public LogObject getLogObject() {
    return logObject;
  }
  
  /**
   * Sets the log object.
   *
   * @param logObject the log object
   */
  public void setLogObject(LogObject logObject) {
    this.logObject = logObject;
  }
  
  /**
   * Gets the selected route.
   *
   * @return the selected route
   */
  public RouteInfo getSelectedRoute() {
    return selectedRoute;
  }
  
  /**
   * Sets the selected route.
   *
   * @param selectedRoute the selected route
   */
  public void setSelectedRoute(RouteInfo selectedRoute) {
    this.selectedRoute = selectedRoute;
  }
  
  /**
   * Gets the method name.
   *
   * @return the method name
   */
  public String getMethodName() {
    return methodName;
  }
  
  /**
   * Sets the method name.
   *
   * @param methodName the method name
   */
  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }
  
}
