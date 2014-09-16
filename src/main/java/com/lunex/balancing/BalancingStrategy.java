package com.lunex.balancing;


import java.util.List;

import com.lunex.util.HostAndPort;

/**
 * @author <a href="http://bruno.factor45.org/">Bruno de Carvalho</a>
 */
public interface BalancingStrategy {

  HostAndPort selectTarget();

  HostAndPort selectTarget(String originHost, int originPort);

  List<HostAndPort> geTargetAddresses();
}
