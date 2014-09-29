package com.lunex.balancing;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.lunex.util.HostAndPort;

/**
 * Class RoundRobinBalacingStrategy implement IBalancingStrategy
 * 
 */
public class RoundRobinStrategy implements IBalancingStrategy {

  private AtomicInteger currentTarget = new AtomicInteger(0);
 
  /**
   * Constructor
   * 
   * @param targets
   */
  public RoundRobinStrategy() {
    this.currentTarget = new AtomicInteger(0);
  }

  public HostAndPort selectTarget(List<HostAndPort> targets) {
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
    }while(targets.get(index).isAlive()==false);
    
    if(!isAlive){
      return null;
    }
    return targets.get(index);
  }

  @Override
  public String toString() {
    return "CurrentTarget: " + currentTarget.toString();
  }
}
