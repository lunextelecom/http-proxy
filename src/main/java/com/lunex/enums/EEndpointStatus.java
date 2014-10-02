package com.lunex.enums;

// TODO: Auto-generated Javadoc
/**
 * The Enum EEndpointStatus.
 */
public enum EEndpointStatus {
  
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
  EEndpointStatus(int p) {
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
