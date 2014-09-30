package com.lunex.util;

import java.io.Serializable;

public class MetricObjectQueue implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = -8354855923280961578L;
 
  private String metric;
  private long metricStartTime;
  private long metricStopTime;
  private String statusResponse;

  public long getMetricStopTime() {
    return metricStopTime;
  }
  public void setMetricStopTime(long metricStopTime) {
    this.metricStopTime = metricStopTime;
  }
  public String getMetric() {
    return metric;
  }
  public void setMetric(String metric) {
    this.metric = metric;
  }
  public long getMetricStartTime() {
    return metricStartTime;
  }
  public void setMetricStartTime(long metricStartTime) {
    this.metricStartTime = metricStartTime;
  }
  public String getStatusResponse() {
    return statusResponse;
  }
  public void setStatusResponse(String statusResponse) {
    this.statusResponse = statusResponse;
  }
  
}
