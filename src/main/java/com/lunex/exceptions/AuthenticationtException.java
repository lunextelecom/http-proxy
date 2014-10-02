package com.lunex.exceptions;

public class AuthenticationtException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  public String getMessage() {
    return "Authentication failed";
  }
}
