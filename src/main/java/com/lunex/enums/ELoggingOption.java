package com.lunex.enums;


public  enum ELoggingOption {
  off("off"), req("req"), req_header("req_header"), req_body("req_body"), resp_header("resp_header"), resp_body("resp_body");
  
  private String option;

  private ELoggingOption(String stringVal) {
    option = stringVal;
  }

  public String toString() {
    return option;
  }

  public static ELoggingOption getELoggingOption(String opt) {
    for (ELoggingOption e : ELoggingOption.values()) {
      if (opt.equalsIgnoreCase(e.option))
        return e;
    }
    return null;
  }
}