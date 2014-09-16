package com.lunex.balancing;


import java.util.List;

import com.lunex.util.HostAndPort;

/**
 * @author <a href="http://bruno.factor45.org/">Bruno de Carvalho</a>
 */
public interface LoadBalancer {

    boolean init();

    void terminate();

    HostAndPort getBalancerAddress();

    List<HostAndPort> getTargetAddresses();

    BalancingStrategy getBalancingStrategy();
}
