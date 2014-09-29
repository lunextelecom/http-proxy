package com.lunex.util;

import com.lunex.enums.EEndpointStatus;

/**
 * The Class EndpointObject.
 */
public class EndpointObject {

  private String target;
  private EEndpointStatus status;
  
  public EndpointObject(String target, EEndpointStatus status){
    this.target = target;
    this.status = status;
  }
  public String getTarget() {
    return target;
  }
  public void setTarget(String target) {
    this.target = target;
  }
  public EEndpointStatus getStatus() {
    return status;
  }
  public void setStatus(EEndpointStatus status) {
    this.status = status;
  }

}
