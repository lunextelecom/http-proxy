package com.lunex.balancing;


import java.util.List;

import com.lunex.util.HostAndPort;

/**
 * Inteface for Balancing Strategy
 * @author BaoLe
 */
public interface IBalancingStrategy {

  HostAndPort selectTarget();

  HostAndPort selectTarget(String originHost, int originPort);

  List<HostAndPort> geTargetAddresses();
}
