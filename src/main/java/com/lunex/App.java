package com.lunex;

import java.util.ArrayList;
import java.util.List;

import com.lunex.http.HttpProxySnoopServer;
import com.lunex.rule.RoutingRule;
import com.lunex.util.RulePattern;
import com.lunex.util.Constants.EBalancingStrategy;

/**
 * Hello world!
 *
 */
public class App {
  public static void main(String[] args) {

    List<RulePattern> listRulePattern = new ArrayList<RulePattern>();
    RulePattern rule = new RulePattern("/didv2/dids?.", EBalancingStrategy.ROUND_ROBIN);
    List<String> targets = new ArrayList<String>();
    targets.add("10.9.9.62:8090");
    targets.add("10.9.9.62:8090");
    rule.createBalancingStrategy(targets);
    listRulePattern.add(rule);
    
    rule = new RulePattern("/atsys/ws/json.", EBalancingStrategy.ROUND_ROBIN);
    targets = new ArrayList<String>();
    targets.add("10.9.9.61:8803");
    targets.add("10.9.9.61:8803");
    rule.createBalancingStrategy(targets);
    listRulePattern.add(rule);

    RoutingRule routingRule = new RoutingRule(listRulePattern);

    final HttpProxySnoopServer server = new HttpProxySnoopServer(8080, routingRule);
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
}
