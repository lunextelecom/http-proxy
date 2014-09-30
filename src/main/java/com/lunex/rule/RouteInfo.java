package com.lunex.rule;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.lunex.enums.ELoggingOption;
import com.lunex.enums.EVerb;
import com.lunex.util.Configuration;

public class RouteInfo implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 7984911931156766485L;

  static final Logger logger = LoggerFactory.getLogger(RouteInfo.class);
  private String name;

  private EVerb verd;
  
  private Pattern pattern = null;
  
  private Map<ELoggingOption, LoggingInfo> loggings;
  
  private String metric;
  
  private String server;

  public void loadConfig(Map<String, String> info, RouteInfo defaultInfo){
    try {
      //name
      if (!Strings.isNullOrEmpty(info.get("name"))) {
        this.name = info.get("name");
      }else{
        if(defaultInfo != null){
          this.name = defaultInfo.getName();
        }
      }
      //server
      if (!Strings.isNullOrEmpty(info.get("server"))) {
        this.server = info.get("server");
      }else{
        if(defaultInfo != null){
          this.server = defaultInfo.getServer();
        }
      }
      //metric
      if (!Strings.isNullOrEmpty(info.get("metric"))) {
        this.metric = info.get("metric");
        if(this.metric.equalsIgnoreCase("off")){
          this.metric = "";
        }
      }else{
        if(defaultInfo != null){
          this.metric = defaultInfo.getMetric();
        }
      }
      //logging
      if (!Strings.isNullOrEmpty(info.get("logging"))) {
        this.loggings = new HashMap<ELoggingOption, LoggingInfo>();
        String tmp = info.get("logging");
        Matcher m = Configuration.getLoggingPattern().matcher(tmp);
        while (m.find()) {
          LoggingInfo loggingInfo = new LoggingInfo();
          loggingInfo.setOpt(ELoggingOption.getELoggingOption(m.group(1).trim()));
          loggingInfo.setVerbs(new HashSet<EVerb>());
          if (!Strings.isNullOrEmpty(m.group(4))) {
            for (String v : m.group(4).split(",")) {
              if (EVerb.ALL.toString().equalsIgnoreCase(v.trim())) {
                loggingInfo.getVerbs().add(EVerb.ALL);
                break;
              }
              EVerb eTmp = EVerb.getEVerd(v.trim());
              if (eTmp != null) {
                loggingInfo.getVerbs().add(eTmp);
              }
            }
          }else{
            loggingInfo.getVerbs().add(EVerb.ALL);
          }
          this.loggings.put(loggingInfo.getOpt(), loggingInfo);
        }
      }else{
        if(defaultInfo != null){
          this.loggings = defaultInfo.getLoggings();
        }
      }
      //url
      if (!Strings.isNullOrEmpty(info.get("url"))) {
        try {
          Matcher matcher = Configuration.getRouteUrlPattern().matcher(info.get("url").trim());
          if(matcher.find()){
            EVerb eTmp = EVerb.getEVerd(matcher.group(1).trim());
            if(eTmp!=null){
              this.verd = eTmp;
            }
            this.pattern = Pattern.compile(matcher.group(2).trim());
          }
        } catch (Exception e) {
          logger.error(e.getMessage());
        }
      }else{
        if(defaultInfo != null){
          this.verd = defaultInfo.getVerd();
          this.pattern = defaultInfo.getPattern();
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }
  
  public static void main(String[] args) {
    Pattern p = Pattern.compile("/home");
    //Matcher m = p.matcher("req, req_body(POST,PUT), resp_body(POST), req(*)");
    Matcher m = p.matcher("/home");
    while (m.find()) {
     System.out.println(1);
      
    }
  }
  /*get*/
  public String getServer() {
    return server;
  }

  public EVerb getVerd() {
    return verd;
  }
  
  public Pattern getPattern() {
    return pattern;
  }
  public String getName() {
    return name;
  }

  public Map<ELoggingOption, LoggingInfo> getLoggings() {
    return loggings;
  }

  public String getMetric() {
    return metric;
  }
  
}
