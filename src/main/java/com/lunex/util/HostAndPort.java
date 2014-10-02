package com.lunex.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

// TODO: Auto-generated Javadoc
/**
 * The Class HostAndPort.
 */
public class HostAndPort {

  // configuration
  // --------------------------------------------------------------------------------------------------

  /** The host. */
  private final String host;
  
  /** The port. */
  private final int port;
  
  /** The url. */
  private final String url;
  
  /** The is alive. */
  private boolean isAlive = true;
  // constructors
  // ---------------------------------------------------------------------------------------------------


  /**
   * The Constructor.
   *
   * @param host the host
   * @param port the port
   * @param url the url
   */
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

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.host + ':' + this.port + this.url;
  }
  
  /**
   * The main method.
   *
   * @param args the args
   */
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

  /**
   * Gets the host.
   *
   * @return the host
   */
  public String getHost() {
    return host;
  }

  /**
   * Gets the url.
   *
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * Gets the port.
   *
   * @return the port
   */
  public int getPort() {
    return port;
  }

  /**
   * Checks if is alive.
   *
   * @return true, if checks if is alive
   */
  public boolean isAlive() {
    return isAlive;
  }

  /**
   * Sets the alive.
   *
   * @param isAlive the alive
   */
  public void setAlive(boolean isAlive) {
    this.isAlive = isAlive;
  }
}
