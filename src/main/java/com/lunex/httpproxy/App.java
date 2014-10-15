package com.lunex.httpproxy;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.lunex.httpproxy.http.HttpProxySnoopServer;
import com.lunex.httpproxy.http.admin.HttpProxyAdminServer;
import com.lunex.httpproxy.scheduler.JobScheduler;
import com.lunex.httpproxy.util.Configuration;


/**
 * The Class App.
 */
public class App {

  /** The Constant logger. */
  static final Logger logger = LoggerFactory.getLogger(App.class);
  
  /** The server. */
  private static HttpProxySnoopServer server;
  
  /** The admin */
  private static HttpProxyAdminServer admin;
  
  private static final String OPTION_APP = "a";

  private static final String OPTION_CONFIG = "c";

  private static final String OPTION_HELP = "h";
  
  private static final String OPTION_LOG4J = "l";
  
  /**
   * The main method.
   *
   * @param args the args
   */
  public static void main(String[] args) {
    final Options options = new Options();
    options.addOption(null, OPTION_APP, true, "app.properties: cassandra, metric, port, admin port....");
    options.addOption(null, OPTION_CONFIG, true, "configuration.yaml: servers, routes info");
    options.addOption(null, OPTION_LOG4J, true, "log4j configuration");
    options.addOption(null, OPTION_HELP, false, "Display command line help.");
    final CommandLineParser parser = new PosixParser();
    final CommandLine cmd;
    try {
      cmd = parser.parse(options, args);
      if (cmd.getArgs().length > 0) {
        throw new UnrecognizedOptionException("Extra arguments were provided in "
            + Arrays.asList(args));
      }
    } catch (final ParseException e) {
      printHelp(options, "Could not parse command line: " + Arrays.asList(args));
      return;
    }
    if (cmd.hasOption(OPTION_HELP) || !cmd.hasOption(OPTION_APP) || !cmd.hasOption(OPTION_CONFIG)) {
      printHelp(options, null);
      return;
    }
    // load log4j properties
    loadLog4j(cmd);
    
    String appConfig = cmd.getOptionValue(OPTION_APP);
    String proxyConfig = cmd.getOptionValue(OPTION_CONFIG);
    try {
      Configuration.loadConfig(appConfig, proxyConfig);
      startHttpProxy();
      startAdmin();
      JobScheduler.run();
      logger.info("startup done, listening....");
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    
  }

  private static void loadLog4j(CommandLine cmd) {
    Properties props = new Properties();
    try {
      if(cmd.hasOption(OPTION_LOG4J)){
        try {
          props.load(new FileInputStream(cmd.getOptionValue(OPTION_LOG4J)));
        } catch (Exception e) {
          props.load(new FileInputStream("conf/log4j.properties"));
        }
      }else{
        props.load(new FileInputStream("conf/log4j.properties"));
      }
      PropertyConfigurator.configure(props);
    } catch (IOException ex) {
      logger.error(ex.getMessage());
    }
  }

  /**
   * Start netty server as HTTP proxy.
   */
  public static void startHttpProxy() {
    if (Configuration.getProxyRule().getRoutes() == null || Configuration.getProxyRule().getRoutes().isEmpty() || Configuration.getProxyPort() <= 0) {
      logger.error("Can not load config or config invalid", new NullPointerException());
      return;
    }
    server = new HttpProxySnoopServer(Configuration.getProxyPort());
    Thread thread = new Thread(new Runnable() {

      public void run() {
        try {
          server.startServer();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    thread.start();
  }
  
  /**
   * Start admin
   */
  public static void startAdmin() {
    if (Configuration.getProxyAdminPort() <= 0) {
      logger.error("Can not load config or config invalid", new NullPointerException());
      return;
    }
    admin = new HttpProxyAdminServer(Configuration.getProxyAdminPort());
    Thread thread = new Thread(new Runnable() {

      public void run() {
        try {
          admin.startServer();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    thread.start();
  }
  
  private static void printHelp(final Options options, final String errorMessage) {
    if (!Strings.isNullOrEmpty(errorMessage)) {
      System.err.println(errorMessage);
    }
    final HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("http-proxy", options);
  }

}
