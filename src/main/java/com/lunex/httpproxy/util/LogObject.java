package com.lunex.httpproxy.util;

import java.io.Serializable;

import com.lunex.httpproxy.rule.RouteInfo.Verb;

// TODO: Auto-generated Javadoc
/**
 * The Class LogObject.
 */
public class LogObject implements Serializable{

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5743754818896473288L;
  
  /** The target. */
  private String target;
  
  /** The request. */
  private String request;
  
  /** The request content. */
  private String requestContent;
  
  /** The request headers. */
  private String requestHeaders;
  
  /** The response content. */
  private String responseContent;
  
  /** The method. */
  private Verb method;
  
  /** The client. */
  private String client;

  /**
   * Gets the target.
   *
   * @return the target
   */
  public String getTarget() {
    return target;
  }

  /**
   * Sets the target.
   *
   * @param target the target
   */
  public void setTarget(String target) {
    this.target = target;
  }

  /**
   * Gets the request.
   *
   * @return the request
   */
  public String getRequest() {
    return request;
  }

  /**
   * Sets the request.
   *
   * @param request the request
   */
  public void setRequest(String request) {
    this.request = request;
  }

  /**
   * Gets the request content.
   *
   * @return the request content
   */
  public String getRequestContent() {
    return requestContent;
  }

  /**
   * Sets the request content.
   *
   * @param requestContent the request content
   */
  public void setRequestContent(String requestContent) {
    this.requestContent = requestContent;
  }

  /**
   * Gets the request headers.
   *
   * @return the request headers
   */
  public String getRequestHeaders() {
    return requestHeaders;
  }

  /**
   * Sets the request headers.
   *
   * @param requestHeaders the request headers
   */
  public void setRequestHeaders(String requestHeaders) {
    this.requestHeaders = requestHeaders;
  }

  /**
   * Gets the response content.
   *
   * @return the response content
   */
  public String getResponseContent() {
    return responseContent;
  }

  /**
   * Sets the response content.
   *
   * @param responseContent the response content
   */
  public void setResponseContent(String responseContent) {
    this.responseContent = responseContent;
  }

  /**
   * Gets the method.
   *
   * @return the method
   */
  public Verb getMethod() {
    return method;
  }

  /**
   * Sets the method.
   *
   * @param method the method
   */
  public void setMethod(Verb method) {
    this.method = method;
  }

  /**
   * Gets the client.
   *
   * @return the client
   */
  public String getClient() {
    return client;
  }

  /**
   * Sets the client.
   *
   * @param client the client
   */
  public void setClient(String client) {
    this.client = client;
  }

}
