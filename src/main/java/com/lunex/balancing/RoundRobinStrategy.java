package com.lunex.balancing;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.lunex.util.HostAndPort;

/**
 * Class RoundRobinBalacingStrategy implement IBalancingStrategy
 * 
 * @author BaoLe
 * @update DuyNguyen
 */
public class RoundRobinStrategy implements IBalancingStrategy {

  private final List<HostAndPort> targets;

  private AtomicInteger currentTarget = new AtomicInteger(0);
  /**
   * Constructor
   * 
   * @param targets
   */
  public RoundRobinStrategy(List<HostAndPort> targets) {
    if ((targets == null) || targets.isEmpty()) {
      throw new IllegalArgumentException("Target list cannot be null or empty");
    }
    this.targets = new CopyOnWriteArrayList<HostAndPort>(targets);
    this.currentTarget = new AtomicInteger(0);
  }

  public HostAndPort selectTarget() {
    Boolean isAlive = false;
    for (HostAndPort server : targets) {
      if(server.isAlive()){
        isAlive = true;
        break;
      }
    }
    if(!isAlive){
      return null;
    }
    int size = targets.size();
    if(size <= 0){
      throw new RuntimeException("Cann't connect any servers!");
    }
    int index = 0;
    int i = -1;
    do{
      index = (currentTarget.getAndAdd(1) + size)%size;
      i++;
      if(i>size){
        isAlive = false;
        break;
      }
    }while(this.targets.get(index).isAlive()==false);
    
    if(!isAlive){
      return null;
    }
    return this.targets.get(index);
  }

  public HostAndPort selectTarget(String originHost, int originPort) {
    return null;
  }

  public List<HostAndPort> geTargetAddresses() {
    return targets;
  }

  @Override
  public String toString() {
    return "targets: " + targets.toString() + ", currentTarget: " + currentTarget.toString();
  }
}
