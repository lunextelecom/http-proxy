package com.lunex.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;

public class CassandraRepository {

  final static Logger logger = LoggerFactory.getLogger(CassandraRepository.class);

  private static CassandraRepository instance = null;
  private Session session;
  private Cluster cluster;
  private Map<String, PreparedStatement> listPreparedStatements;

  private CassandraRepository() {

  }

  /**
   * Get instance
   * 
   * @author BaoLe
   * @return
   */
  public static CassandraRepository getInstance() {
    if (instance == null) {
      instance = new CassandraRepository();
    }
    return instance;
  }

  /**
   * Init connection
   * 
   * @author BaoLe
   * @param serverIP
   * @param keyspace
   * @return
   */
  public CassandraRepository initConnectionCassandraDB(String serverIP, String keyspace) {
    try {
      cluster = Cluster.builder().addContactPoints(serverIP).build();
      session = cluster.connect(keyspace);
      listPreparedStatements = new HashMap<String, PreparedStatement>();
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
   * Execute query
   * 
   * @author BaoLe
   * @param sql
   * @param listParams nullable
   * @return
   */
  public ResultSet executeQueryWithParams(String sql, List<Object> listParams) throws Exception {
    if (sql == Constants.EMPTY_STRING) {
      return null;
    }
    String cqlStatement = sql;
    try {
      ResultSet data = null;
      if (listParams != null) {
        PreparedStatement statement = null;
        if (!listPreparedStatements.keySet().contains(cqlStatement)) {
          statement = session.prepare(cqlStatement);
          listPreparedStatements.put(cqlStatement, statement);
        } else {
          statement = listPreparedStatements.get(cqlStatement);
        }
        BoundStatement boundStatement = new BoundStatement(statement);
        data = session.execute(boundStatement.bind(listParams.toArray()));
      } else {
        data = session.execute(cqlStatement);
      }
      return data;
    } catch (Exception ex) {
      logger.error(ex.getMessage());
      throw ex;
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
  public boolean executeQueryWithParamsNonQuery(String sql, List<Object> listParams)
      throws Exception {
    if (sql == Constants.EMPTY_STRING) {
      return false;
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
        session.execute(boundStatement.bind(listParams.toArray()));
      } else {
        session.execute(cqlStatement);
      }
      return true;
    } catch (Exception ex) {
      logger.error(ex.getMessage());
      throw ex;
    }
  }

}
