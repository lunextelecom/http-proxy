package com.lunex.rule;

import java.util.HashSet;
import java.util.Set;

public class IpFilterRule {

  private Set<String> hosts = new HashSet<>();

  public Set<String> getHosts() {
    return hosts;
  }

  public void setHosts(Set<String> hosts) {
    this.hosts = hosts;
  }


  
}
