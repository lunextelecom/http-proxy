package com.lunex.enums;


// TODO: Auto-generated Javadoc
/**
 * The Enum ELoggingOption.
 */
public  enum ELoggingOption {
  
  /** The off. */
  off("off"), 
 /** The req. */
 req("req"), 
 /** The req_header. */
 req_header("req_header"), 
 /** The req_body. */
 req_body("req_body"), 
 /** The resp_header. */
 resp_header("resp_header"), 
 /** The resp_body. */
 resp_body("resp_body");
  
  /** The option. */
  private String option;

  /**
   * The Constructor.
   *
   * @param stringVal the string val
   */
  private ELoggingOption(String stringVal) {
    option = stringVal;
  }

  /* (non-Javadoc)
   * @see java.lang.Enum#toString()
   */
  public String toString() {
    return option;
  }

  /**
   * Gets the e logging option.
   *
   * @param opt the opt
   * @return the e logging option
   */
  public static ELoggingOption getELoggingOption(String opt) {
    for (ELoggingOption e : ELoggingOption.values()) {
      if (opt.equalsIgnoreCase(e.option))
        return e;
    }
    return null;
  }
}