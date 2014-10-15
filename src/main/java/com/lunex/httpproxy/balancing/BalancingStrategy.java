package com.lunex.httpproxy.balancing;


import java.util.List;

import com.lunex.httpproxy.util.HostAndPort;

/**
 * Inteface for Balancing Strategy
 * 
 */
public interface BalancingStrategy {

  /**
   * Select target
   * 
   * @return
   */
  HostAndPort selectTarget(List<HostAndPort> targets);
  
}
