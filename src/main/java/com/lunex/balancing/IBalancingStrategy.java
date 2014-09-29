package com.lunex.balancing;


import java.util.List;

import com.lunex.util.HostAndPort;

/**
 * Inteface for Balancing Strategy
 * 
 */
public interface IBalancingStrategy {

  /**
   * Select target
   * 
   * @return
   */
  HostAndPort selectTarget(List<HostAndPort> targets);
  
}
