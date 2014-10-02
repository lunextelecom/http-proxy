package com.lunex.util;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class MetricObjectQueue.
 */
public class MetricObjectQueue implements Serializable{

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8354855923280961578L;
 
  /** The metric. */
  private String metric;
  
  /** The metric start time. */
  private long metricStartTime;
  
  /** The metric stop time. */
  private long metricStopTime;
  
  /** The status response. */
  private String statusResponse;

  /**
   * Gets the metric stop time.
   *
   * @return the metric stop time
   */
  public long getMetricStopTime() {
    return metricStopTime;
  }
  
  /**
   * Sets the metric stop time.
   *
   * @param metricStopTime the metric stop time
   */
  public void setMetricStopTime(long metricStopTime) {
    this.metricStopTime = metricStopTime;
  }
  
  /**
   * Gets the metric.
   *
   * @return the metric
   */
  public String getMetric() {
    return metric;
  }
  
  /**
   * Sets the metric.
   *
   * @param metric the metric
   */
  public void setMetric(String metric) {
    this.metric = metric;
  }
  
  /**
   * Gets the metric start time.
   *
   * @return the metric start time
   */
  public long getMetricStartTime() {
    return metricStartTime;
  }
  
  /**
   * Sets the metric start time.
   *
   * @param metricStartTime the metric start time
   */
  public void setMetricStartTime(long metricStartTime) {
    this.metricStartTime = metricStartTime;
  }
  
  /**
   * Gets the status response.
   *
   * @return the status response
   */
  public String getStatusResponse() {
    return statusResponse;
  }
  
  /**
   * Sets the status response.
   *
   * @param statusResponse the status response
   */
  public void setStatusResponse(String statusResponse) {
    this.statusResponse = statusResponse;
  }
  
}
