package com.lunex.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.lunex.balancing.IBalancingStrategy;
import com.lunex.balancing.RoundRobinStrategy;
import com.lunex.enums.EBalancingType;
import com.lunex.util.Configuration;
import com.lunex.util.HostAndPort;

public class ServerInfo implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = -6983449929369275996L;

  static final Logger logger = LoggerFactory.getLogger(ServerInfo.class);
 
  private String name;

  private EBalancingType balancingType;
  
  private IBalancingStrategy balancingStrategy;

  private List<HostAndPort> targets;
  
  private String health = "PING";

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

  /*get*/
  public String getName() {
    return name;
  }

  public EBalancingType getBalancingType() {
    return balancingType;
  }

  public IBalancingStrategy getBalancingStrategy() {
    return balancingStrategy;
  }

  public List<HostAndPort> getTargets() {
    return targets;
  }

  public String getHealth() {
    return health;
  }
 
}
