package com.lunex.util;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

/**
 * Utils
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
  
  // HTTP GET request
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
