package com.lunex;


import java.util.ArrayList;
import java.util.List;

import com.lunex.balancing.DefaultLoadBalancer;
import com.lunex.balancing.RoundRobinBalancingStrategy;
import com.lunex.util.HostAndPort;

/**
 * @author <a href="http://bruno.factor45.org/">Bruno de Carvalho</a>
 */
public class Malabarista {

  // public static methods
  // ------------------------------------------------------------------------------------------

  public static void printUsage() {
    System.err.println("Usage:");
    System.err.println(Malabarista.class.getSimpleName());
    System.err
        .println("    -B<ipv4>:<port>: Balancer address (mandatory) - Examples: '-B10.0.0.1:80' or '-B*:80'");
    System.err.println("    -T<ipv4>:<port>: Target address (at least one required)");
    System.err.println("    -S<string>: Target selection strategy ('rr' = Round Robin, "
        + "'fair' = Fair (favor equal number of connections per target) balance)");
  }

  // private classes
  // ------------------------------------------------------------------------------------------------

  private static class CommandLineOptions {

    // internal vars
    // ----------------------------------------------------------------------------------------------

    private HostAndPort balancerAddress;
    private List<HostAndPort> destinationAddresses;
    private String selectionStrategy;

    // constructors
    // -----------------------------------------------------------------------------------------------

    private CommandLineOptions() {}

    // public static methods
    // --------------------------------------------------------------------------------------

    public static CommandLineOptions parseCommandLineOptions(String[] args) {
      CommandLineOptions options = new CommandLineOptions();
      HostAndPort address;

      for (String arg : args) {
        if (arg.length() < 3) {
          System.err.println("Invalid argument ignored: " + arg);
          continue;
        }
        switch (arg.charAt(1)) {
          case 'b':
          case 'B':
            address = HostAndPort.decode(arg.substring(2));
            if (address == null) {
              continue;
            }
            options.balancerAddress = address;
            break;
          case 't':
          case 'T':
            address = HostAndPort.decode(arg.substring(2));
            if (address == null) {
              continue;
            }
            if (options.destinationAddresses == null) {
              options.destinationAddresses = new ArrayList<HostAndPort>();
            }
            options.destinationAddresses.add(address);
            break;
          case 's':
          case 'S':
            options.selectionStrategy = arg.substring(2);
            break;
          default:
            System.err.println("Unknown command line option ignored: " + arg);
        }
      }

      return options;
    }

    // getters & setters
    // ------------------------------------------------------------------------------------------

    public HostAndPort getBalancerAddress() {
      return balancerAddress;
    }

    public List<HostAndPort> getDestinationAddresses() {
      return destinationAddresses;
    }

    public String getSelectionStrategy() {
      return selectionStrategy;
    }

    // low level overrides
    // --------------------------------------------------------------------------------------------

    @Override
    public String toString() {
      return new StringBuilder().append("CommandLineOptions{").append("balancerAddress=")
          .append(balancerAddress).append(", destinationAddresses=").append(destinationAddresses)
          .append(", selectionStrategy='").append(selectionStrategy).append('\'').append('}')
          .toString();
    }
  }

  // main
  // -----------------------------------------------------------------------------------------------------------

  public static void main(String[] args) {
    args = "-b*:8080 -t10.9.9.62:8090 -t10.9.9.62:8090 -sRR".split(" ");
    CommandLineOptions options = CommandLineOptions.parseCommandLineOptions(args);
    if (options.getBalancerAddress() == null) {
      System.err.println("Balancer address argument cannot be null.");
      printUsage();
    }

    if ((options.getDestinationAddresses() == null) || options.getDestinationAddresses().isEmpty()) {
      System.err.println("At least one destination address must be provided.");
      printUsage();
    }

    RoundRobinBalancingStrategy strategy =
        new RoundRobinBalancingStrategy(options.getDestinationAddresses());
    final DefaultLoadBalancer loadBalancer =
        new DefaultLoadBalancer("defaultLoadBalancer", options.getBalancerAddress(), strategy);

    if (!loadBalancer.init()) {
      System.err.println("Failed to launch LoadBalancer with options: " + options);
      return;
    }

    Thread shutdownHook = new Thread() {

      @Override
      public void run() {
        loadBalancer.terminate();
      }
    };
    Runtime.getRuntime().addShutdownHook(shutdownHook);
  }
}
