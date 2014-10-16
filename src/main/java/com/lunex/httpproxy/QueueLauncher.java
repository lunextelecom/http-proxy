package com.lunex.httpproxy;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.lunex.httpproxy.util.Configuration;


/**
 * The Class HttpProxyLauncher.
 */
public class QueueLauncher {

  /** The Constant logger. */
  static final Logger logger = LoggerFactory.getLogger(QueueLauncher.class);
  
  private static final String OPTION_APP = "a";

  private static final String OPTION_HELP = "h";
  
  /**
   * The main method.
   */
  public static void main(String[] args) {
    final Options options = new Options();
    options.addOption(null, OPTION_APP, true, "app.properties: cassandra, metric");
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
    if (cmd.hasOption(OPTION_HELP) || !cmd.hasOption(OPTION_APP)) {
      printHelp(options, null);
      return;
    }
    
    String appConfig = cmd.getOptionValue(OPTION_APP);
    try {
      Configuration.loadQueueConf(appConfig);
      logger.info("startup done, listening....");
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    
  }
  
  private static void printHelp(final Options options, final String errorMessage) {
    if (!Strings.isNullOrEmpty(errorMessage)) {
      System.err.println(errorMessage);
    }
    final HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("http-proxy", options);
  }

}
