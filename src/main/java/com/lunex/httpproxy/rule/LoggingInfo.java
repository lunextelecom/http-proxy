package com.lunex.httpproxy.rule;

import java.io.Serializable;
import java.util.Set;

import com.lunex.httpproxy.rule.RouteInfo.Verb;

// TODO: Auto-generated Javadoc
/**
 * The Class LoggingInfo.
 */
public class LoggingInfo implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2760630342324006892L;

  /** The verbs. */
  private Set<Verb> verbs;
  
  /** The opt. */
  private LoggingOption opt;//off, req, req_header, req_body, resp_header, resp_body

  /**
   * Checks if is write logging.
   *
   * @param option the option
   * @param method the method
   * @return true, if checks if is write logging
   */
  public boolean isWriteLogging(LoggingOption option, String method){
    boolean res = false;
    if(opt != null && opt != LoggingOption.off && opt == option){
      if(verbs== null || verbs.contains(Verb.ALL) || verbs.contains(Verb.getEVerd(method))){
        res = true;
      }
    }
    return res;
  }

  public  enum LoggingOption {
    
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
    private LoggingOption(String stringVal) {
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
    public static LoggingOption getELoggingOption(String opt) {
      for (LoggingOption e : LoggingOption.values()) {
        if (opt.equalsIgnoreCase(e.option))
          return e;
      }
      return null;
    }
  }
  
  /*get, set*/
  /**
   * Gets the verbs.
   *
   * @return the verbs
   */
  public Set<Verb> getVerbs() {
    return verbs;
  }


  /**
   * Sets the verbs.
   *
   * @param verbs the verbs
   */
  public void setVerbs(Set<Verb> verbs) {
    this.verbs = verbs;
  }


  /**
   * Gets the opt.
   *
   * @return the opt
   */
  public LoggingOption getOpt() {
    return opt;
  }

  /**
   * Sets the opt.
   *
   * @param opt the opt
   */
  public void setOpt(LoggingOption opt) {
    this.opt = opt;
  }
  
}
