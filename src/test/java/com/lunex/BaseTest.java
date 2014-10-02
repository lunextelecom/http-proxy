package com.lunex;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.ConstantReconnectionPolicy;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;
import com.lunex.util.Configuration;


public class BaseTest {
  private static String node = "localhost";
  Cluster cluster;
  Session session;

  @BeforeClass
  public static void oneTimeSetUp() {
    // one-time initialization code
    System.out.println("@BeforeClass - oneTimeSetUp");
    initEnviroment();
    Configuration.loadConfig("app.properties");
  }

  @AfterClass
  public static void oneTimeTearDown() {
    // one-time cleanup code
    System.out.println("@AfterClass - oneTimeTearDown");

  }

  @Before
  public void setUp() {
    System.out.println("@Before - setUp");

    Builder builder = Cluster.builder();
    builder.addContactPoint(node);// .withPort(Configuration.getPort());

    PoolingOptions options = new PoolingOptions();
    options.setCoreConnectionsPerHost(HostDistance.LOCAL,
        options.getMaxConnectionsPerHost(HostDistance.LOCAL));
    builder.withPoolingOptions(options);

    cluster =
        builder.withRetryPolicy(DowngradingConsistencyRetryPolicy.INSTANCE)
            .withReconnectionPolicy(new ConstantReconnectionPolicy(100L)).build();

    session = cluster.connect();
  }

  @After
  public void tearDown() {
    System.out.println("@After - tearDown");
    cluster.close();
    discardData();

  }

  private static void initEnviroment() {
    Builder builder = Cluster.builder();
    builder.addContactPoint(node);// .withPort(Configuration.getPort());

    PoolingOptions options = new PoolingOptions();
    options.setCoreConnectionsPerHost(HostDistance.LOCAL,
        options.getMaxConnectionsPerHost(HostDistance.LOCAL));
    builder.withPoolingOptions(options);

    Cluster cluster =
        builder.withRetryPolicy(DowngradingConsistencyRetryPolicy.INSTANCE)
            .withReconnectionPolicy(new ConstantReconnectionPolicy(100L)).build();

    Session session = cluster.connect();

    Metadata metadata = cluster.getMetadata();
    String keyspace = "http_proxy";
    KeyspaceMetadata keyspaceMetadata = metadata.getKeyspace(keyspace);
    if (keyspaceMetadata == null) {
      String sql =
          "CREATE KEYSPACE http_proxy WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 }";
      session.execute(sql);
      metadata = cluster.getMetadata();
      keyspaceMetadata = metadata.getKeyspace(keyspace);
    }
    if (metadata != null) {
      keyspaceMetadata = metadata.getKeyspace(keyspace);
      if (keyspaceMetadata == null) {
        throw new UnsupportedOperationException("Can't find keyspace :" + keyspace);
      }
      if (keyspaceMetadata.getTable("logging") == null) {
        String sql =
            "CREATE TABLE http_proxy.logging (target text, url text, updateid timeuuid, client text, method text, request_header text, request_body text, response_body text, PRIMARY KEY (target, url, updateid)) WITH CLUSTERING ORDER BY (url ASC, updateid DESC)";
        session.execute(sql);
      }
      if (keyspaceMetadata.getTable("endpoint") == null) {
        String sql =
            "CREATE TABLE http_proxy.endpoint (target text, status int, updateid timeuuid, PRIMARY KEY (target))";
        session.execute(sql);
      }
      
    }
    cluster.close();
  }

  private static void discardData() {
    Builder builder = Cluster.builder();
    builder.addContactPoint(node);

    PoolingOptions options = new PoolingOptions();
    options.setCoreConnectionsPerHost(HostDistance.LOCAL,
        options.getMaxConnectionsPerHost(HostDistance.LOCAL));
    builder.withPoolingOptions(options);

    Cluster cluster =
        builder.withRetryPolicy(DowngradingConsistencyRetryPolicy.INSTANCE)
            .withReconnectionPolicy(new ConstantReconnectionPolicy(100L)).build();

    Session session = cluster.connect();
    String sql = "truncate http_proxy.logging";
    session.execute(sql);
    sql = "truncate http_proxy.endpoint";
    session.execute(sql);

    cluster.close();
  }
}
