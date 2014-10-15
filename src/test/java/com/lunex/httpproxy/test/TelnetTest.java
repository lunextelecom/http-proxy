package com.lunex.httpproxy.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.lunex.httpproxy.util.Utils;


public class TelnetTest {

  @Test
  public void testTelnet() {
    
    Boolean res = Utils.checkServerAlive("10.9.9.61", 8000);
    assertEquals(true, res);
    
    res = Utils.checkServerAlive("10.9.9.61", 8001);
    assertEquals(false, res);
  }

}
