package com.lunex.enums;

public enum EEndpointStatus {
  ALIVE(1), DOWN(0);   
  int status;
  EEndpointStatus(int p) {
    status = p;
  }
  public int value() {
     return status;
  } 
}
