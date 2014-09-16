package com.lunex.balancing;


import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.lunex.util.HostAndPort;

/**
 * Class RoundRobinBalacingStrategy implement IBalancingStrategy
 * 
 * @author BaoLe
 */
public class RoundRobinBalancingStrategy implements IBalancingStrategy {

  private final List<HostAndPort> targets;
  private final AtomicInteger currentTarget;

  /**
   * Constructor
   * 
   * @param targets
   */
  public RoundRobinBalancingStrategy(List<HostAndPort> targets) {
    if ((targets == null) || targets.isEmpty()) {
      throw new IllegalArgumentException("Target list cannot be null or empty");
    }
    this.targets = new CopyOnWriteArrayList<HostAndPort>(targets);
    this.currentTarget = new AtomicInteger(0);
  }

  public HostAndPort selectTarget() {
    int currentTarget;
    synchronized (this.currentTarget) {
      currentTarget = this.currentTarget.getAndIncrement();
      if (currentTarget >= this.targets.size()) {
        currentTarget = 0;
        this.currentTarget.set(0);
      }
    }
    return this.targets.get(currentTarget);
  }

  public HostAndPort selectTarget(String originHost, int originPort) {
    return null;
  }

  public List<HostAndPort> geTargetAddresses() {
    return Collections.unmodifiableList(this.targets);
  }

  @Override
  public String toString() {
    return "targets: " + targets.toString() + ", currentTarget: " + currentTarget.toString();
  }
}
