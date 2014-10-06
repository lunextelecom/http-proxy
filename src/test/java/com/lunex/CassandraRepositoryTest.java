package com.lunex;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.lunex.cassandra.CassandraRepository;
import com.lunex.enums.EEndpointStatus;
import com.lunex.enums.EVerb;
import com.lunex.util.EndpointObject;
import com.lunex.util.LogObject;


public class CassandraRepositoryTest extends BaseTest {

  @Test
  public void testInsertLogging() {
    Boolean thrown = false;
    try {
    String target = "10.9.9.61:8080";
    LogObject obj = new LogObject();
    obj.setTarget(target);
    obj.setClient("192.168.1.213");
    obj.setMethod(EVerb.GET);
    obj.setRequest("/login");
    obj.setRequestContent("requestContent");
    obj.setRequestHeaders("requestHeaders");
    obj.setResponseContent("responseContent");
    CassandraRepository.getInstance().insertLogging(obj);
    String sql = "select count(1) from http_proxy.logging where target = ?";
    List<Object> params = new ArrayList<>();
    params.add(target);
      assertEquals(true,CassandraRepository.getInstance().execute(sql, params).one().getLong(0)==1);
    } catch (Exception e) {
      thrown = true;
    }
    assertEquals(false, thrown);
  }

  @Test
  public void testUpdateEndpoint() {
    Boolean thrown = false;
    try {
    String target = "10.9.9.61:8080";
    EndpointObject obj = new EndpointObject(target, EEndpointStatus.ALIVE);
    CassandraRepository.getInstance().updateEndpoint(obj);
    String sql = "select target, status from http_proxy.endpoint where target = ?";
    List<Object> params = new ArrayList<>();
    params.add(target);
      assertEquals(true,CassandraRepository.getInstance().execute(sql, params).one().getInt("status") == EEndpointStatus.ALIVE.value());
      obj.setStatus(EEndpointStatus.DOWN);
      CassandraRepository.getInstance().updateEndpoint(obj);
      assertEquals(false,CassandraRepository.getInstance().execute(sql, params).one().getInt("status") == EEndpointStatus.ALIVE.value());
    } catch (Exception e) {
      thrown = true;
    }
    assertEquals(false, thrown);
  }

}
