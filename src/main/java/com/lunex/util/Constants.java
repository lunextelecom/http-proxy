package com.lunex.util;

public class Constants {

  public static String EMPTY_STRING = "";
  
  public static enum EBalancingStrategy {
    ROUND_ROBIN
  }

  public static enum ELoggingOption {
    request, request_header, request_body, response_body, on, off
  }
  
  public static enum EVerb {
    GET, POST, DELETE, NONE
  }
}
