package com.lunex.httpproxy.util;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

// TODO: Auto-generated Javadoc
/**
 * Utils.
 */
public class Utils {
  
  /**
   * Check server alive.
   *
   * @param host the host
   * @param port the port
   * @return true, if check server alive
   */
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
  
  // HTTP GET request
  /**
   * Check server alive.
   *
   * @param url the url
   * @return true, if check server alive
   */
  public static boolean checkServerAlive(String url) {
    try {
      HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
      con.setRequestMethod("HEAD");
      con.setConnectTimeout(2000); // set timeout to 2 seconds
      return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
    } catch (Exception e) {
      return false;
    }
  }
  
}
