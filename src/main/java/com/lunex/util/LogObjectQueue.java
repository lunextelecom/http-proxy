package com.lunex.util;

import java.io.Serializable;

import com.lunex.rule.RouteInfo;

public class LogObjectQueue implements Serializable {

  
  /**
   * 
   */
  private static final long serialVersionUID = 835531349906551492L;

  private LogObject logObject;
  private RouteInfo selectedRoute;
  private String methodName;
  
  public LogObject getLogObject() {
    return logObject;
  }
  public void setLogObject(LogObject logObject) {
    this.logObject = logObject;
  }
  public RouteInfo getSelectedRoute() {
    return selectedRoute;
  }
  public void setSelectedRoute(RouteInfo selectedRoute) {
    this.selectedRoute = selectedRoute;
  }
  public String getMethodName() {
    return methodName;
  }
  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }
  
}
