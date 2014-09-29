package com.lunex.rule;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.lunex.enums.ELoggingOption;

/**
 * Class ProxyRule
 * 
 */
public class ProxyRule {
  
  private ServerInfo serverDefault;
  
  private RouteInfo routeDefault;
  
  private Map<String, ServerInfo> servers;
  
  private List<RouteInfo> routes;

  public RouteInfo selectRouteInfo(String url) {
    for (RouteInfo child : routes) {
      if (child.getPattern() != null) {
        Matcher m = child.getPattern().matcher(url);
        if (m.find()) {
          return child;
        }
      }
    }
    return null;
  }
  
  public ServerInfo selectServerInfo(RouteInfo route) {
    return servers.get(route.getServer());
  }

  public boolean isOffLogging(RouteInfo route) {
    boolean res = false;
    Map<ELoggingOption, LoggingInfo> lstLoggings = route.getLoggings();
    if(lstLoggings == null || lstLoggings.isEmpty()){
      res = true;
    }
    if(!res){
      if(lstLoggings.containsKey(ELoggingOption.off)){
        res = true;
      }
    }
    return res;
  }
  
  /*get, set*/
  public ServerInfo getServerDefault() {
    return serverDefault;
  }

  public void setServerDefault(ServerInfo serverDefault) {
    this.serverDefault = serverDefault;
  }

  public Map<String, ServerInfo> getServers() {
    return servers;
  }

  public void setServers(Map<String, ServerInfo> servers) {
    this.servers = servers;
  }

  public RouteInfo getRouteDefault() {
    return routeDefault;
  }

  public void setRouteDefault(RouteInfo routeDefault) {
    this.routeDefault = routeDefault;
  }

  public List<RouteInfo> getRoutes() {
    return routes;
  }

  public void setRoutes(List<RouteInfo> routes) {
    this.routes = routes;
  }

}
