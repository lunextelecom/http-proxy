package com.lunex.httpproxy.util;

/**
 * The Class EndpointObject.
 */
public class EndpointObject {

  /** The target. */
  private String target;
  
  /** The status. */
  private EndpointStatus status;
  
  /**
   * The Constructor.
   *
   * @param target the target
   * @param status the status
   */
  public EndpointObject(String target, EndpointStatus status){
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
  public EndpointStatus getStatus() {
    return status;
  }
  
  /**
   * Sets the status.
   *
   * @param status the status
   */
  public void setStatus(EndpointStatus status) {
    this.status = status;
  }

  public enum EndpointStatus {
    
    /** The alive. */
    ALIVE(1), 
   /** The down. */
   DOWN(0);   
    
    /** The status. */
    int status;
    
    /**
     * The Constructor.
     *
     * @param p the p
     */
    EndpointStatus(int p) {
      status = p;
    }
    
    /**
     * Value.
     *
     * @return the int
     */
    public int value() {
       return status;
    } 
  }

}
