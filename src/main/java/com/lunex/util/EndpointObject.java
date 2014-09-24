package com.lunex.util;

import com.lunex.util.Constants.EEndpointStatus;

/**
 * The Class EndpointObject.
 * @author DuyNguyen
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
