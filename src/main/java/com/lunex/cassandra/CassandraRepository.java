package com.lunex.cassandra;

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
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.ConstantReconnectionPolicy;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;
import com.lunex.util.Configuration;
import com.lunex.util.Constants;
import com.lunex.util.EndpointObject;
import com.lunex.util.LogObject;

public class CassandraRepository {

  static final Logger logger = LoggerFactory.getLogger(CassandraRepository.class);

  private static CassandraRepository instance = null;
  private Session session;
  private Cluster cluster;
  private Map<String, PreparedStatement> listPreparedStatements;

  /**
   * Get instance
   * 
   * @author BaoLe
   * @update DuyNguyen
   * @return
   */
  public static CassandraRepository getInstance() {
    if (instance == null) {
      instance = init(Configuration.getHost(), Configuration.getKeyspace());
    }
    return instance;
  }

  /**
   * Init connection
   * 
   * @author BaoLe
   * @update DuyNguyen
   * @param serverIP
   * @param keyspace
   * @return
   */
  private static CassandraRepository init(String serverIP, String keyspace) {
    try {
      instance = new CassandraRepository();
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

      instance.session = instance.cluster.connect(keyspace);
      instance.listPreparedStatements = new HashMap<String, PreparedStatement>();
      return instance;
    } catch (Exception ex) {
      logger.error(ex.getMessage());
      return null;
    }
  }

  /**
   * Close connect
   * 
   * @author BaoLe
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
   * Insert logging.
   * @author DuyNguyen
   * @param logObject the log object
   */
  public void insertLogging(LogObject logObject){
    String sql = "insert into logging(target, url, updateid, method, client, request_header, request_body, response_body) values (?, ?, now(), ?, ?, ?, ?, ?)";
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
   * @author DuyNguyen
   * @param endpointObject the endpoint object
   */
  public void updateEndpoint(EndpointObject endpointObject){
    String updateStatus = "insert into endpoint(target, status, updateid) values(?,?,now())";
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
   * @author BaoLe
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
