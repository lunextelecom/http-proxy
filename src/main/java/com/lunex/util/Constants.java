package com.lunex.util;


public class Constants {

  public static String EMPTY_STRING = "";
  
  public static String AUTH_STR = "ADMIN";
  
  public static String USERNAME_PRO = "Username";
  public static String PASSWORD_PRO = "Password";
  
  public static enum EBalancingStrategy {
    ROUND_ROBIN
  }
  
  public static enum EEndpointStatus {
    ALIVE(1), DOWN(0);   
    int status;
    EEndpointStatus(int p) {
      status = p;
    }
    public int value() {
       return status;
    } 
 }

  public static enum ELoggingOption {
    request, request_header, request_body, response_body, on, off
  }
  
  public static enum EVerb {
    GET, POST, DELETE, NONE
  }
}
