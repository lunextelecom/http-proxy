package com.lunex.scheduler;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.common.base.Strings;
import com.lunex.cassandra.CassandraRepository;
import com.lunex.enums.EEndpointStatus;
import com.lunex.rule.ProxyRule;
import com.lunex.rule.RouteInfo;
import com.lunex.rule.ServerInfo;
import com.lunex.util.Configuration;
import com.lunex.util.EndpointObject;
import com.lunex.util.HostAndPort;
import com.lunex.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class TelnetJob.
 */
public class TelnetJob implements Job {

  /** The log. */
  private Logger log = Logger.getLogger(TelnetJob.class);

  /* (non-Javadoc)
   * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
   */
  public void execute(JobExecutionContext jExeCtx) throws JobExecutionException {
    log.info("start telnet job");
    ProxyRule proxyRule = Configuration.getProxyRule();
    if (proxyRule != null) {
      for (RouteInfo rule : proxyRule.getRoutes()) {
        ServerInfo serverInfo = proxyRule.getServers().get(rule.getServer());
        if(serverInfo != null){
          String health = serverInfo.getHealth();
          if(Strings.isNullOrEmpty(health)){
            health = "ping";
          }
          if(!health.equalsIgnoreCase("off")){
            List<HostAndPort> targets = serverInfo.getTargets();
            if(targets != null && targets.size()>0){
              if(health.equalsIgnoreCase("ping")){
                for (HostAndPort item : targets) {
                  boolean oldStatus = item.isAlive();
                  boolean status = Utils.checkServerAlive(item.getHost(), item.getPort());
                  if(!status){
                    if(oldStatus){
                      try {
                        Thread.sleep(100);
                        status =  Utils.checkServerAlive(item.getHost(), item.getPort());
                        if(!status){
                          item.setAlive(false);
                          log.info("Can not connected with ip " + item.getHost() + " and port " + item.getPort());
                          EndpointObject endpointObject = new EndpointObject(item.toString(), EEndpointStatus.DOWN);
                          CassandraRepository.getInstance().updateEndpoint(endpointObject);
                        }
                      } catch (Exception e) {
                        log.error(e.getMessage());
                      }
                    }
                  }else{
                    if(!oldStatus){
                      log.info("Connected with ip " + item.getHost() + " and port " + item.getPort());
                      item.setAlive(true);
                      try {
                        EndpointObject endpointObject = new EndpointObject(item.toString(), EEndpointStatus.ALIVE);
                        CassandraRepository.getInstance().updateEndpoint(endpointObject);
                      } catch (Exception e) {
                        log.error(e.getMessage());
                      }
                    }
                  }
                }
              }else{
                for (HostAndPort item : targets) {
                  boolean oldStatus = item.isAlive();
                  String url = String.format("http://%s:%d%s%s", item.getHost(), item.getPort(), item.getUrl(), health);       
                  boolean status = Utils.checkServerAlive(url);
                  if(!status){
                    if(oldStatus){
                      try {
                        Thread.sleep(100);
                        status =  Utils.checkServerAlive(url);
                        if(!status){
                          item.setAlive(false);
                          log.info("Check health FAILED with :" + url);
                          EndpointObject endpointObject = new EndpointObject(item.toString(), EEndpointStatus.DOWN);
                          CassandraRepository.getInstance().updateEndpoint(endpointObject);
                        }
                      } catch (Exception e) {
                        log.error(e.getMessage());
                      }
                    }
                  }else{
                    if(!oldStatus){
                      log.info("Check health OK with :" + url);
                      item.setAlive(true);
                      try {
                        EndpointObject endpointObject = new EndpointObject(item.toString(), EEndpointStatus.ALIVE);
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
        }
      }
    }
    log.info("end telnet job");
  }
}
