package com.lunex.rule;

import java.io.Serializable;
import java.util.Set;

import com.lunex.enums.ELoggingOption;
import com.lunex.enums.EVerb;

// TODO: Auto-generated Javadoc
/**
 * The Class LoggingInfo.
 */
public class LoggingInfo implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2760630342324006892L;

  /** The verbs. */
  private Set<EVerb> verbs;
  
  /** The opt. */
  private ELoggingOption opt;//off, req, req_header, req_body, resp_header, resp_body

  /**
   * Checks if is write logging.
   *
   * @param option the option
   * @param method the method
   * @return true, if checks if is write logging
   */
  public boolean isWriteLogging(ELoggingOption option, String method){
    boolean res = false;
    if(opt != null && opt != ELoggingOption.off && opt == option){
      if(verbs== null || verbs.contains(EVerb.ALL) || verbs.contains(EVerb.getEVerd(method))){
        res = true;
      }
    }
    return res;
  }


  /*get, set*/
  /**
   * Gets the verbs.
   *
   * @return the verbs
   */
  public Set<EVerb> getVerbs() {
    return verbs;
  }


  /**
   * Sets the verbs.
   *
   * @param verbs the verbs
   */
  public void setVerbs(Set<EVerb> verbs) {
    this.verbs = verbs;
  }


  /**
   * Gets the opt.
   *
   * @return the opt
   */
  public ELoggingOption getOpt() {
    return opt;
  }

  /**
   * Sets the opt.
   *
   * @param opt the opt
   */
  public void setOpt(ELoggingOption opt) {
    this.opt = opt;
  }
  
}
