package com.lunex.httpproxy.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Hello world!
 *
 */
public class HttpClientTest {
  private final String USER_AGENT = "Mozilla/5.0";
  private final String AUTH = "admin";

  public static void main(String[] args) throws Exception {

    HttpClientTest http = new HttpClientTest();

    System.out.println("Testing 1 - Send Http GET request");
    http.sendGet();

  }

  // HTTP GET request
  private void sendGet() throws Exception {
    String url = "http://127.0.0.1:8080/health";

    URL obj = new URL(url);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

    // optional default is GET
    con.setRequestMethod("GET");

    // add request header
    con.setRequestProperty("User-Agent", USER_AGENT);
//    con.setRequestProperty("Username", AUTH);
    con.setRequestProperty("Password", AUTH);

    int responseCode = con.getResponseCode();
    System.out.println(con.getResponseMessage());
    System.out.println("\nSending 'GET' request to URL : " + url);
    System.out.println("Response Code : " + responseCode);

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();

    // print result
    System.out.println(response.toString());

  }

  // HTTP POST request
  private void sendPost() throws Exception {

    String url = "https://selfsolve.apple.com/wcResults.do";
    URL obj = new URL(url);
    HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

    // add reuqest header
    con.setRequestMethod("POST");
    con.setRequestProperty("User-Agent", USER_AGENT);
    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

    String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

    // Send post request
    con.setDoOutput(true);
    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
    wr.writeBytes(urlParameters);
    wr.flush();
    wr.close();

    int responseCode = con.getResponseCode();
    System.out.println("\nSending 'POST' request to URL : " + url);
    System.out.println("Post parameters : " + urlParameters);
    System.out.println("Response Code : " + responseCode);

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();

    // print result
    System.out.println(response.toString());

  }
}
