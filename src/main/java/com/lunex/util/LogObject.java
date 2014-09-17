package com.lunex.util;

import com.lunex.util.Constants.EVerb;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;

public class LogObject {

  private String target;
  private HttpRequest request;
  private HttpContent requestContent;
  private HttpHeaders requestHeaders;
  private HttpContent responseContent;
  private EVerb method;

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public HttpRequest getRequest() {
    return request;
  }

  public void setRequest(HttpRequest request) {
    this.request = request;
  }

  public HttpContent getRequestContent() {
    return requestContent;
  }

  public void setRequestContent(HttpContent requestContent) {
    this.requestContent = requestContent;
  }

  public HttpHeaders getRequestHeaders() {
    return requestHeaders;
  }

  public void setRequestHeaders(HttpHeaders requestHeaders) {
    this.requestHeaders = requestHeaders;
  }

  public HttpContent getResponseContent() {
    return responseContent;
  }

  public void setResponseContent(HttpContent responseContent) {
    this.responseContent = responseContent;
  }

  public EVerb getMethod() {
    return method;
  }

  public void setMethod(EVerb method) {
    this.method = method;
  }

}
