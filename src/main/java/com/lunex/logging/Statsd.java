package com.lunex.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.util.Constants;
import com.lunex.util.ParameterHandler;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

/**
 * Statsd client for write metric
 * 
 */
public class Statsd {

  final static Logger logger = LoggerFactory.getLogger(Statsd.class);
  private StatsDClient client = null;
  private long start = 0;

  /**
   * Create StatsD client for write metric
   * 
   * @param metric
   * @param host
   * @param port
   * @return
   */
  public static StatsDClient getStatsd(String metric, String host, int port) {
    String selectedMetric = Constants.EMPTY_STRING;
    if (!Constants.EMPTY_STRING.equals(metric)) {
      selectedMetric = metric;
    } else {
      return null;
    }
    String selectedHost = ParameterHandler.METRIC_HOST;
    if (!Constants.EMPTY_STRING.equals(host)) {
      selectedHost = host;
    }
    int selectedPort = ParameterHandler.METRIC_PORT;
    if (port != 0) {
      selectedPort = port;
    }
    StatsDClient client = new NonBlockingStatsDClient(selectedMetric, selectedHost, selectedPort);
    return client;
  }

  /**
   * Constructor
   * 
   * @param metric
   * @param start
   * @param host
   * @param port
   */
  public Statsd(String metric, long start, String host, int port) {
    this.client = Statsd.getStatsd(metric, host, port);
    this.start = start;
  }

  /**
   * Constructor
   * 
   * @param client
   * @param start
   */
  public Statsd(StatsDClient client, long start) {
    this.client = client;
    this.start = start;
  }

  /**
   * Start measure metric
   * 
   * @param metric
   * @param host
   * @param port
   * @return
   */
  public static Statsd start(String metric, String host, int port) {
    return new Statsd(metric, System.currentTimeMillis(), host, port);
  }
  
  public static Statsd start(String metric, long start, String host, int port) {
    return new Statsd(metric, start, host, port);
  }

  /**
   * Stop measure and write metric
   * 
   * @param aspect
   * @return
   */
  public int stop(String aspect) {
    if (this.client != null) {
      long end = System.currentTimeMillis();
      int t = (int) (end - this.start);
      this.client.time(aspect, t);
      try {
        this.client.stop();
        logger.info("statsd.stop {} {}", aspect, t);
      } catch (Exception ex) {
      }
      return t;
    }
    return 0;
  }
  public int stop(String aspect, long end) {
    if (this.client != null) {
      int t = (int) (end - this.start);
      this.client.time(aspect, t);
      try {
        this.client.stop();
        logger.info("statsd.stop {} {}", aspect, t);
      } catch (Exception ex) {
      }
      return t;
    }
    return 0;
  }
  public static void main(String[] args) {
    try {
      Statsd client = Statsd.start("test.http_proxy.health_server.get_list", "192.168.93.112", 8125);
      Thread.sleep(1000);
      client.stop("200");
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
