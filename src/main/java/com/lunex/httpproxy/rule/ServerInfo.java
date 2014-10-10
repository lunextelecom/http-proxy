package com.lunex.httpproxy.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.lunex.httpproxy.balancing.BalancingStrategy;
import com.lunex.httpproxy.balancing.LeastUseStrategy;
import com.lunex.httpproxy.balancing.RoundRobinStrategy;
import com.lunex.httpproxy.util.Configuration;
import com.lunex.httpproxy.util.HostAndPort;

// TODO: Auto-generated Javadoc
/**
 * The Class ServerInfo.
 */
public class ServerInfo implements Serializable {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6983449929369275996L;

  /** The Constant logger. */
  static final Logger logger = LoggerFactory.getLogger(ServerInfo.class);
 
  /** The name. */
  private String name;

  /** The balancing type. */
  private BalancingType balancingType;
  
  /** The balancing strategy. */
  private BalancingStrategy balancingStrategy;

  /** The targets. */
  private List<HostAndPort> targets;
  
  /** The health. */
  private String health = "PING";

  /**
   * Load config.
   *
   * @param info the info
   * @param defaultInfo the default info
   */
  public void loadConfig(Map<String, String> info, ServerInfo defaultInfo){
    //load server
    try {
      if (!Strings.isNullOrEmpty(info.get("name"))) {
        name = info.get("name");
      } else {
        if (defaultInfo != null) {
          name = defaultInfo.getName();
        }
      }
      if (!Strings.isNullOrEmpty(info.get("health"))) {
        health = info.get("health");
      } else {
        if (defaultInfo != null) {
          health = defaultInfo.getHealth();
        }
      }
      if (!Strings.isNullOrEmpty(info.get("balancer"))) {
        balancingType = Configuration.MAP_BALANCER_STATEGY.get(info.get("balancer").toUpperCase());
        switch (balancingType) {
          case ROUND_ROBIN:
            balancingStrategy = new RoundRobinStrategy();
            break;
          case LEAST_USE:
            balancingStrategy = new LeastUseStrategy();
            break;
          default:
            break;
        }
      } else {
        if (defaultInfo != null) {
          balancingType = defaultInfo.getBalancingType();
          balancingStrategy = defaultInfo.getBalancingStrategy();
        }
      }
      if (!Strings.isNullOrEmpty(info.get("target"))) {
        targets = new ArrayList<HostAndPort>();
        for (String child : info.get("target").split(",")) {
          Matcher matcher = Configuration.getTargetPattern().matcher(child.trim());
          if (matcher.find()) {
            targets.add(new HostAndPort(matcher.group(1), Integer.parseInt(Strings.isNullOrEmpty(matcher.group(3))?"80":matcher.group(3)),
                matcher.group(4)));
          }
        }
      } else {
        if (defaultInfo != null) {
          targets = defaultInfo.getTargets();
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  public enum BalancingType {
    
    /** The round robin. */
    ROUND_ROBIN, 
   /** The least use. */
   LEAST_USE
  }
  
  /*get*/
  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the balancing type.
   *
   * @return the balancing type
   */
  public BalancingType getBalancingType() {
    return balancingType;
  }

  /**
   * Gets the balancing strategy.
   *
   * @return the balancing strategy
   */
  public BalancingStrategy getBalancingStrategy() {
    return balancingStrategy;
  }

  /**
   * Gets the targets.
   *
   * @return the targets
   */
  public List<HostAndPort> getTargets() {
    return targets;
  }

  /**
   * Gets the health.
   *
   * @return the health
   */
  public String getHealth() {
    return health;
  }
 
}
