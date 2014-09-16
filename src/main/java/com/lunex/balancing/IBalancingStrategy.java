package com.lunex.balancing;


import java.util.List;

import com.lunex.util.HostAndPort;

/**
 * Inteface for Balancing Strategy
 * 
 * @author BaoLe
 */
public interface IBalancingStrategy {

  /**
   * Select target
   * 
   * @author BaoLe
   * @return
   */
  HostAndPort selectTarget();

  /**
   * Select target
   * 
   * @author BaoLe
   * @param originHost
   * @param originPort
   * @return
   */
  HostAndPort selectTarget(String originHost, int originPort);

  /**
   * Get list target
   * 
   * @author BaoLe
   * @return
   */
  List<HostAndPort> geTargetAddresses();
}
