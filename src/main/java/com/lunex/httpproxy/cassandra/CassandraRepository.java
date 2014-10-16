package com.lunex.httpproxy.cassandra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.ConstantReconnectionPolicy;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;
import com.google.common.base.Strings;
import com.lunex.httpproxy.util.Configuration;
import com.lunex.httpproxy.util.Constants;
import com.lunex.httpproxy.util.EndpointObject;
import com.lunex.httpproxy.util.LogObject;

public class CassandraRepository {

  static final Logger logger = LoggerFactory.getLogger(CassandraRepository.class);

  private static CassandraRepository instance = null;
  private Session session;
  private Cluster cluster;
  private String keyspace;
  private Map<String, PreparedStatement> listPreparedStatements;

  /**
   * Get instance
   * 
   * @return
   */
  public static CassandraRepository getInstance() throws Exception {
    if (instance == null) {
      instance = init(Configuration.getHost(), Configuration.getKeyspace());
    }
    return instance;
  }

  /**
   * Init connection
   * 
   * @param serverIP
   * @param keyspace
   * @return
   */
  private static CassandraRepository init(String serverIP, String keyspace) throws Exception {
      instance = new CassandraRepository();
      if(Strings.isNullOrEmpty(keyspace)){
        instance.keyspace = "http_proxy";
      }else{
        instance.keyspace = keyspace.trim();
      }
      Builder builder = Cluster.builder();
      builder.addContactPoint(serverIP);

      PoolingOptions options = new PoolingOptions();
      options.setCoreConnectionsPerHost(HostDistance.LOCAL,
              options.getMaxConnectionsPerHost(HostDistance.LOCAL));
      builder.withPoolingOptions(options);

      instance.cluster = builder
              .withRetryPolicy(
                      DowngradingConsistencyRetryPolicy.INSTANCE)
              .withReconnectionPolicy(
                      new ConstantReconnectionPolicy(100L)).build();

      instance.session = instance.cluster.connect();
      Metadata metadata =  instance.cluster.getMetadata();
      if (metadata != null) {
        KeyspaceMetadata keyspaceMetadata = metadata.getKeyspace(instance.keyspace);
        if (keyspaceMetadata == null) {
          throw new UnsupportedOperationException("Can't find keyspace :" + instance.keyspace);
        }
        if (keyspaceMetadata.getTable("logging") == null) {
          throw new UnsupportedOperationException("Can't find table logging in " + instance.keyspace);
        }
        if (keyspaceMetadata.getTable("endpoint") == null) {
          throw new UnsupportedOperationException("Can't find table endpoint in " + instance.keyspace);
        }
        
      }
      instance.listPreparedStatements = new HashMap<String, PreparedStatement>();
      return instance;
  }

  /**
   * Close connect
   * 
   * @return
   */
  public boolean closeConnection() {
    try {
      session.close();
      cluster.close();
      return true;
    } catch (Exception ex) {
      logger.error(ex.getMessage());
      return false;
    }
  }

  /**
   * select endpoint.
   * 
   */
  public ResultSet getLstEndpointInfo(){
    String sql = "select * from " + instance.keyspace + ".endpoint";
    List<Object> listParams = new ArrayList<Object>();
    ResultSet res = null;
    try {
      res = execute(sql, listParams);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    return res;
  }
  
  
  /**
   * Insert logging.
   * @param logObject the log object
   */
  public void insertLogging(LogObject logObject){
    String sql = "insert into " + instance.keyspace + ".logging(target, url, updateid, method, client, request_header, request_body, response_body) values (?, ?, now(), ?, ?, ?, ?, ?)";
    List<Object> listParams = new ArrayList<Object>();
    listParams.add(logObject.getTarget());
    listParams.add(logObject.getRequest());
    listParams.add(logObject.getMethod().toString());
    listParams.add(logObject.getClient());
    listParams.add(logObject.getRequestHeaders());
    listParams.add(logObject.getRequestContent());
    listParams.add(logObject.getResponseContent());
    try {
      execute(sql, listParams);

    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }
  
  /**
   * Update endpoint.
   * @param endpointObject the endpoint object
   */
  public void updateEndpoint(EndpointObject endpointObject){
    String updateStatus = "insert into " + instance.keyspace + ".endpoint(target, status, updateid) values(?,?,now())";
    List<Object> listParams = new ArrayList<Object>();
    listParams.add(endpointObject.getTarget());
    listParams.add(endpointObject.getStatus().value());
    try {
      execute(updateStatus, listParams);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * Execute non query
   * 
   * @param sql
   * @param listParams
   * @return
   */
  public ResultSet execute(String sql, List<Object> listParams)
      throws Exception {
    ResultSet res = null;
    if (sql == Constants.EMPTY_STRING) {
      return null;
    }
    String cqlStatement = sql;
    try {
      if (listParams != null) {
        PreparedStatement statement = null;
        if (!listPreparedStatements.keySet().contains(cqlStatement)) {
          statement = session.prepare(cqlStatement);
          listPreparedStatements.put(cqlStatement, statement);
        } else {
          statement = listPreparedStatements.get(cqlStatement);
        }
        BoundStatement boundStatement = new BoundStatement(statement);
        res = session.execute(boundStatement.bind(listParams.toArray()));
      } else {
        res = session.execute(cqlStatement);
      }
    } catch (Exception ex) {
      logger.error(ex.getMessage());
      throw ex;
    }
    return res;
  }

}
