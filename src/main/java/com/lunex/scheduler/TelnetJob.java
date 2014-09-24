package com.lunex.scheduler;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lunex.balancing.RoundRobinStrategy;
import com.lunex.cassandra.CassandraRepository;
import com.lunex.rule.RoutingRule;
import com.lunex.rule.RoutingRulePattern;
import com.lunex.util.Configuration;
import com.lunex.util.Constants.EEndpointStatus;
import com.lunex.util.EndpointObject;
import com.lunex.util.HostAndPort;
import com.lunex.util.Utils;

public class TelnetJob implements Job {

  private Logger log = Logger.getLogger(TelnetJob.class);

  public void execute(JobExecutionContext jExeCtx) throws JobExecutionException {
    log.info("start telnet job");
    RoutingRule routingRule = Configuration.getRoutingRule();
    if (routingRule != null) {
      for (RoutingRulePattern rule : routingRule.getListRulePattern()) {
        if (rule.getBalancingStrategy() instanceof RoundRobinStrategy) {
          RoundRobinStrategy tmp = (RoundRobinStrategy) rule.getBalancingStrategy();
          for (HostAndPort server : tmp.geTargetAddresses()) {
            boolean oldStatus = server.isAlive();
            boolean status = Utils.checkServerAlive(server.getHost(), server.getPort());
            if(!status){
              if(oldStatus){
                try {
                  Thread.sleep(100);
                  status =  Utils.checkServerAlive(server.getHost(), server.getPort());
                  if(!status){
                    server.setAlive(false);
                    log.info("Can not connected with ip " + server.getHost() + " and port " + server.getPort());
                    EndpointObject endpointObject = new EndpointObject(server.toString(), EEndpointStatus.DOWN);
                    CassandraRepository.getInstance().updateEndpoint(endpointObject);
                  }
                } catch (Exception e) {
                  log.error(e.getMessage());
                }
              }
            }else{
              if(!oldStatus){
                log.info("Connected with ip " + server.getHost() + " and port " + server.getPort());
                server.setAlive(true);
                try {
                  EndpointObject endpointObject = new EndpointObject(server.toString(), EEndpointStatus.ALIVE);
                  CassandraRepository.getInstance().updateEndpoint(endpointObject);
                } catch (Exception e) {
                  log.error(e.getMessage());
                }
              }
            }
          }
        }
      }
    }
    log.info("end telnet job");
  }
}
