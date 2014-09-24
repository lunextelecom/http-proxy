package com.lunex.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.lunex.cassandra.CassandraRepository;
import com.lunex.rule.IpFilterRule;
import com.lunex.rule.LoggingRule;
import com.lunex.rule.MetricRule;
import com.lunex.rule.RoutingRule;
import com.lunex.util.Constants.EBalancingStrategy;

/**
 * Utils
 * 
 * @udpate DuyNguyen
 *
 */
public class Utils {
  public static boolean checkServerAlive(String host, int port){
    boolean res = true;
    try {
      Socket s1 = new Socket();
      s1.connect(new InetSocketAddress(host, port), 1000);
      
      InputStream is = s1.getInputStream();
      DataInputStream dis = new DataInputStream(is);
      dis.close();
      s1.close();
    } catch (Exception e) {
      res = false;
    }
    return res;
  }
  
}
