package com.lunex.balancing;


import java.util.List;

import com.lunex.util.HostAndPort;

public interface ILoadBalancer {

    boolean init();

    void terminate();

    HostAndPort getBalancerAddress();

    List<HostAndPort> getTargetAddresses();

    IBalancingStrategy getBalancingStrategy();
}
