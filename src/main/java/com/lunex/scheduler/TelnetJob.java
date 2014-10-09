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
            if (targets != null && targets.size() > 0) {
              for (HostAndPort item : targets) {
                boolean oldStatus = item.isAlive();
                boolean status = checkStatus(item, health, oldStatus);
                if (!status & oldStatus) {
                  item.setAlive(false);
                  log.info("Can not connected with ip " + item.getHost() + " and port " + item.getPort());
                  updateEndpoint(item, EEndpointStatus.DOWN);
                } else if (status & !oldStatus) {
                  log.info("Connected with ip " + item.getHost() + " and port " + item.getPort());
                  item.setAlive(true);
                  updateEndpoint(item, EEndpointStatus.ALIVE);
                }else{
                  if(!Configuration.getLstTargets().contains(item.toString())){
                    Configuration.getLstTargets().add(item.toString());
                    updateEndpoint(item, status?EEndpointStatus.ALIVE:EEndpointStatus.DOWN);
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
  private boolean checkStatus(HostAndPort item, String health, boolean oldStatus){
    boolean status = false;
    String url = String.format("http://%s:%d%s%s", item.getHost(), item.getPort(),item.getUrl(), health);
    if (health.equalsIgnoreCase("ping")) {
      status = Utils.checkServerAlive(item.getHost(), item.getPort());
    } else {
      status = Utils.checkServerAlive(url);
    }
    if (!status) {
      if (oldStatus) {
        try {
          Thread.sleep(100);
          if (health.equalsIgnoreCase("ping")) {
            status = Utils.checkServerAlive(item.getHost(), item.getPort());
          } else {
            status = Utils.checkServerAlive(url);
          }
        }catch (Exception e) {
          log.error(e.getMessage());
        }
      }
    }
    return status;
  }
  
  private void updateEndpoint(HostAndPort item, EEndpointStatus status){
    try {
      EndpointObject endpointObject =new EndpointObject(item.toString(), status);
      CassandraRepository.getInstance().updateEndpoint(endpointObject);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }
  
}
