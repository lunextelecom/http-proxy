package com.lunex.httpproxy.queue;

import java.io.IOException;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.httpproxy.cassandra.CassandraRepository;
import com.lunex.httpproxy.logging.LoggingProcessor;
import com.lunex.httpproxy.logging.Statsd;
import com.lunex.httpproxy.util.Configuration;
import com.lunex.httpproxy.util.EndpointObject;
import com.lunex.httpproxy.util.HostAndPort;
import com.lunex.httpproxy.util.LogObjectQueue;
import com.lunex.httpproxy.util.MetricObjectQueue;
import com.lunex.httpproxy.util.ParameterHandler;
import com.lunex.httpproxy.util.EndpointObject.EndpointStatus;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;


/**
 * The endpoint that consumes messages off of the queue. Happens to be runnable.
 * 
 */
public class QueueConsumer extends EndPoint implements Runnable, Consumer {

  static final Logger logger = LoggerFactory.getLogger(QueueConsumer.class);
  
  public QueueConsumer(String endPointName) throws IOException {
    super(endPointName);
  }

  public void run() {
    try {
      // start consuming messages. Auto acknowledge messages.
      channel.basicConsume(endPointName, true, this);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Called when consumer is registered.
   */
  public void handleConsumeOk(String consumerTag) {
    System.out.println("Consumer " + consumerTag + " registered");
  }

  /**
   * Called when new message is available.
   */
  public void handleDelivery(String consumerTag, Envelope env, BasicProperties props, byte[] body)
      throws IOException {
    Object obj = (Object) SerializationUtils.deserialize(body);
    if (obj instanceof MetricObjectQueue) {
      logger.info("received MetricObjectQueue");
      MetricObjectQueue objInfo = (MetricObjectQueue) obj;
      Statsd statsd = Statsd.start(objInfo.getMetric(), objInfo.getMetricStartTime(), Configuration.getMetricHost(), Configuration.getMetricPort());
      statsd.stop(objInfo.getStatusResponse(), objInfo.getMetricStopTime());
    }else if (obj instanceof EndpointObject) {
      logger.info("received EndpointObject");
      EndpointObject objInfo = (EndpointObject) obj;
      updateEndpoint(objInfo);
    } else {
      logger.info("received LogObjectQueue");
      LogObjectQueue objInfo = (LogObjectQueue) obj;
      LoggingProcessor.writeLogging(objInfo.getMethodName(), objInfo.getLogObject(), objInfo.getSelectedRoute());
    }

  }
  private void updateEndpoint(EndpointObject endpointObject){
    try {
      CassandraRepository.getInstance().updateEndpoint(endpointObject);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }
  public void handleCancel(String consumerTag) {}

  public void handleCancelOk(String consumerTag) {}

  public void handleRecoverOk(String consumerTag) {}

  public void handleShutdownSignal(String consumerTag, ShutdownSignalException arg1) {}
}
