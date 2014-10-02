package com.lunex.util;

import com.lunex.enums.EEndpointStatus;

// TODO: Auto-generated Javadoc
/**
 * The Class EndpointObject.
 */
public class EndpointObject {

  /** The target. */
  private String target;
  
  /** The status. */
  private EEndpointStatus status;
  
  /**
   * The Constructor.
   *
   * @param target the target
   * @param status the status
   */
  public EndpointObject(String target, EEndpointStatus status){
    this.target = target;
    this.status = status;
  }
  
  /**
   * Gets the target.
   *
   * @return the target
   */
  public String getTarget() {
    return target;
  }
  
  /**
   * Sets the target.
   *
   * @param target the target
   */
  public void setTarget(String target) {
    this.target = target;
  }
  
  /**
   * Gets the status.
   *
   * @return the status
   */
  public EEndpointStatus getStatus() {
    return status;
  }
  
  /**
   * Sets the status.
   *
   * @param status the status
   */
  public void setStatus(EEndpointStatus status) {
    this.status = status;
  }

}
