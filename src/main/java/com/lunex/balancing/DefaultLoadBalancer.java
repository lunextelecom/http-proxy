package com.lunex.balancing;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.http.HttpProxySnoopServer;
import com.lunex.util.HostAndPort;

/**
 * @author <a href="http://bruno.factor45.org/">Bruno de Carvalho</a>
 */
public class DefaultLoadBalancer implements ILoadBalancer {

    // constants ------------------------------------------------------------------------------------------------------

    private static final Logger LOG = LoggerFactory.getLogger(DefaultLoadBalancer.class);
    private static final int TIMEOUT_IN_MILLIS = 3000;

    // configuration --------------------------------------------------------------------------------------------------

    private final String id;
    private final HostAndPort balancerAddress;
    private final IBalancingStrategy balancingStrategy;
    private final Executor bossPool;
    private final Executor workerPool;
    private int timeoutInMillis;

    // internal vars --------------------------------------------------------------------------------------------------

    private final boolean internalPools;
    private volatile boolean running;
    private Channel acceptor;
    private ChannelGroup allChannels;
    private ServerBootstrap bootstrap;
    private HttpProxySnoopServer server;

    // constructors ---------------------------------------------------------------------------------------------------

    public DefaultLoadBalancer(String id, HostAndPort balancerAddress, IBalancingStrategy balancingStrategy) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (balancerAddress == null) {
            throw new IllegalArgumentException("Balancer local address cannot be null");
        }

        if (balancingStrategy == null) {
            throw new IllegalArgumentException("Balancing strategy cannot be null");
        }

        this.id = id;
        this.balancerAddress = balancerAddress;
        this.balancingStrategy = balancingStrategy;

        this.internalPools = true;
        this.bossPool = Executors.newCachedThreadPool();
        this.workerPool = Executors.newCachedThreadPool();

        this.timeoutInMillis = TIMEOUT_IN_MILLIS;
    }

    public DefaultLoadBalancer(String id, HostAndPort balancerAddress, IBalancingStrategy balancingStrategy,
                               Executor bossPool, Executor workerPool) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        if (balancerAddress == null) {
            throw new IllegalArgumentException("Balancer local address cannot be null");
        }

        if (balancingStrategy == null) {
            throw new IllegalArgumentException("Balancing strategy cannot be null");
        }

        if (bossPool == null) {
            throw new IllegalArgumentException("BossPool cannot be null");
        }

        if (workerPool == null) {
            throw new IllegalArgumentException("WorkerPool cannot be null");
        }

        this.id = id;
        this.balancerAddress = balancerAddress;
        this.balancingStrategy = balancingStrategy;

        this.internalPools = true;
        this.bossPool = Executors.newCachedThreadPool();
        this.workerPool = Executors.newCachedThreadPool();

        this.timeoutInMillis = TIMEOUT_IN_MILLIS;
    }

    // LoadBalancer ---------------------------------------------------------------------------------------------------

    public synchronized boolean init() {
        if (this.running) {
            return true;
        }

        LOG.info("Launching {} on {}...", this, this.balancerAddress);

        Thread th = new Thread(new Runnable() {
          
          public void run() {
            // TODO Auto-generated method stub
//            server = new InputProcessorHttpSnoopServer(balancerAddress.getPort(), false, balancingStrategy);
            try {
              server.startServer();
            } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            
//        if (!bound) {
//            LOG.error("Failed to bound {} to {}.", this, this.balancerAddress);
//        } else {
//            LOG.info("Successfully bound {} to {}.", this, this.balancerAddress);
//        }
            
          }
        });
        th.start();

        return (this.running = true);
    }

    public synchronized void terminate() {
        if (!this.running) {
            return;
        }

        LOG.info("Shutting down {}...", this.id);
        this.running = false;

        this.allChannels.close().awaitUninterruptibly();
        this.acceptor.close().awaitUninterruptibly();
        // never close the thread pool, that's a responsability for whoever provided it
        if (this.internalPools) {
            
        }
        server.stopServer();
        LOG.info("{} stopped.", this.id);
    }

    public HostAndPort getBalancerAddress() {
        return this.balancerAddress;
    }

    public List<HostAndPort> getTargetAddresses() {
        return this.balancingStrategy.geTargetAddresses();
    }

    public IBalancingStrategy getBalancingStrategy() {
        return this.balancingStrategy;
    }

    // low level overrides --------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '@' + Integer.toHexString(this.hashCode());
    }
}
