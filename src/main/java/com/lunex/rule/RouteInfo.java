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

// TODO: Auto-generated Javadoc
/**
 * The Class RouteInfo.
 */
public class RouteInfo implements Serializable {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7984911931156766485L;

  /** The Constant logger. */
  static final Logger logger = LoggerFactory.getLogger(RouteInfo.class);
  
  /** The name. */
  private String name;

  /** The verd. */
  private EVerb verd;
  
  /** The pattern. */
  private Pattern pattern = null;
  
  /** The loggings. */
  private Map<ELoggingOption, LoggingInfo> loggings;
  
  /** The metric. */
  private String metric;
  
  /** The server. */
  private String server;

  /**
   * Load config.
   *
   * @param info the info
   * @param defaultInfo the default info
   */
  public void loadConfig(Map<String, String> info, RouteInfo defaultInfo) throws Exception{
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
          throw new Exception("invalid pattern:" + info.get("url"), e);
        }
      }else{
        if(defaultInfo != null){
          this.verd = defaultInfo.getVerd();
          this.pattern = defaultInfo.getPattern();
        }
      }
    } catch (Exception e) {
      throw new Exception("load route failed:", e);
    }
  }
  
  /**
   * The main method.
   *
   * @param args the args
   */
  public static void main(String[] args) {
    Pattern p = Pattern.compile("/home");
    //Matcher m = p.matcher("req, req_body(POST,PUT), resp_body(POST), req(*)");
    Matcher m = p.matcher("/home");
    while (m.find()) {
     System.out.println(1);
      
    }
  }
  /*get*/
  /**
   * Gets the server.
   *
   * @return the server
   */
  public String getServer() {
    return server;
  }

  /**
   * Gets the verd.
   *
   * @return the verd
   */
  public EVerb getVerd() {
    return verd;
  }
  
  /**
   * Gets the pattern.
   *
   * @return the pattern
   */
  public Pattern getPattern() {
    return pattern;
  }
  
  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the loggings.
   *
   * @return the loggings
   */
  public Map<ELoggingOption, LoggingInfo> getLoggings() {
    return loggings;
  }

  /**
   * Gets the metric.
   *
   * @return the metric
   */
  public String getMetric() {
    return metric;
  }
  
}
