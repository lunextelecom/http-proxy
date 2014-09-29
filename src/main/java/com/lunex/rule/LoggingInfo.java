package com.lunex.rule;

import io.netty.handler.codec.http.HttpMethod;

import java.util.Set;

import com.lunex.enums.ELoggingOption;
import com.lunex.enums.EVerb;

public class LoggingInfo {

  private Set<EVerb> verbs;
  
  private ELoggingOption opt;//off, req, req_header, req_body, resp_header, resp_body

  public boolean isWriteLogging(ELoggingOption option, HttpMethod method){
    boolean res = false;
    if(opt != null && opt != ELoggingOption.off && opt == option){
      if(verbs== null || verbs.contains(EVerb.ALL) || verbs.contains(EVerb.getEVerd(method.name()))){
        res = true;
      }
    }
    return res;
  }


  /*get, set*/
  public Set<EVerb> getVerbs() {
    return verbs;
  }


  public void setVerbs(Set<EVerb> verbs) {
    this.verbs = verbs;
  }


  public ELoggingOption getOpt() {
    return opt;
  }

  public void setOpt(ELoggingOption opt) {
    this.opt = opt;
  }
  
}
