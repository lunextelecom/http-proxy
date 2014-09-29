package com.lunex.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

public class HostAndPort {

  // configuration
  // --------------------------------------------------------------------------------------------------

  private final String host;
  private final int port;
  private final String url;
  private boolean isAlive = true;
  // constructors
  // ---------------------------------------------------------------------------------------------------


  public HostAndPort(String host, int port, String url) {
    if ((port < 0) || (port > 65536)) {
      throw new IllegalArgumentException("Port must be in range 0-65536");
    }
    this.host = host;
    this.port = port;
    if(!Strings.isNullOrEmpty(url)){
      this.url = url;
    }else{
      this.url = "";
    }
  }

  // low level overrides
  // --------------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return this.host + ':' + this.port + this.url;
  }
  public static void main(String[] args) {
    Pattern pattern = Pattern.compile("([^:^/]*):(\\d*)?(.*)?");
    Matcher matcher = pattern.matcher("10.9.9.61:8080");

    matcher.find();

    String domain   = matcher.group(1);
    String port     = matcher.group(2);
    String uri      = matcher.group(3);
    System.out.println(domain);
    System.out.println(port);
    System.out.println(uri);
    
    pattern= Pattern.compile("(\\w*)([(](.*)[)])*");
    matcher = pattern.matcher("post");

    matcher.find();

    System.out.println(matcher.group(1));
    System.out.println(matcher.group(2));
    System.out.println(matcher.group(3));
  }
  
  // getters & setters
  // ----------------------------------------------------------------------------------------------

  public String getHost() {
    return host;
  }

  public String getUrl() {
    return url;
  }

  public int getPort() {
    return port;
  }

  public boolean isAlive() {
    return isAlive;
  }

  public void setAlive(boolean isAlive) {
    this.isAlive = isAlive;
  }
}
