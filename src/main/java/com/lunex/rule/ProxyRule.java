package com.lunex.rule;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.lunex.enums.ELoggingOption;

// TODO: Auto-generated Javadoc
/**
 * Class ProxyRule.
 */
public class ProxyRule implements Serializable{
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2537140267845430254L;

  /** The server default. */
  private ServerInfo serverDefault;
  
  /** The route default. */
  private RouteInfo routeDefault;
  
  /** The servers. */
  private Map<String, ServerInfo> servers;
  
  /** The routes. */
  private List<RouteInfo> routes;

  /**
   * Select route info.
   *
   * @param url the url
   * @return the route info
   */
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
  
  /**
   * Select server info.
   *
   * @param route the route
   * @return the server info
   */
  public ServerInfo selectServerInfo(RouteInfo route) {
    return servers.get(route.getServer());
  }

  /**
   * Checks if is off logging.
   *
   * @param route the route
   * @return true, if checks if is off logging
   */
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
  /**
   * Gets the server default.
   *
   * @return the server default
   */
  public ServerInfo getServerDefault() {
    return serverDefault;
  }

  /**
   * Sets the server default.
   *
   * @param serverDefault the server default
   */
  public void setServerDefault(ServerInfo serverDefault) {
    this.serverDefault = serverDefault;
  }

  /**
   * Gets the servers.
   *
   * @return the servers
   */
  public Map<String, ServerInfo> getServers() {
    return servers;
  }

  /**
   * Sets the servers.
   *
   * @param servers the servers
   */
  public void setServers(Map<String, ServerInfo> servers) {
    this.servers = servers;
  }

  /**
   * Gets the route default.
   *
   * @return the route default
   */
  public RouteInfo getRouteDefault() {
    return routeDefault;
  }

  /**
   * Sets the route default.
   *
   * @param routeDefault the route default
   */
  public void setRouteDefault(RouteInfo routeDefault) {
    this.routeDefault = routeDefault;
  }

  /**
   * Gets the routes.
   *
   * @return the routes
   */
  public List<RouteInfo> getRoutes() {
    return routes;
  }

  /**
   * Sets the routes.
   *
   * @param routes the routes
   */
  public void setRoutes(List<RouteInfo> routes) {
    this.routes = routes;
  }

}
