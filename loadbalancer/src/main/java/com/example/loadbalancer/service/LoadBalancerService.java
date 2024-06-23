package com.example.loadbalancer.service;

import com.example.loadbalancer.controller.LoadBalancerController;
import com.example.loadbalancer.model.BackendServer;
import com.example.loadbalancer.strategy.LoadBalancingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoadBalancerService {
    private final ConcurrentHashMap<Integer, BackendServer> servers;
    private LoadBalancingStrategy strategy;

    private static final Logger logger = LoggerFactory.getLogger(LoadBalancerService.class);

    @Autowired
    public LoadBalancerService(ConcurrentHashMap<Integer, BackendServer> servers, LoadBalancingStrategy strategy) {
        this.servers = servers;
        this.strategy = strategy;
    }

    public void setStrategy(LoadBalancingStrategy strategy) {
        this.strategy = strategy;
    }

    public BackendServer getNextServer() {
        try {
            logger.info("Selecting next backend server using {}", strategy.getClass().getSimpleName());
            BackendServer server = strategy.getServer(servers);
            logger.info("Selected backend server: {}", server.getUrl());
            return server;
        } catch (Exception e) {
            logger.error("Error selecting backend server", e);
            throw new RuntimeException("Failed to select backend server", e);
        }
    }


    public void addServer(BackendServer server) {
        servers.put(server.hashCode(),server);
    }

    public void removeServer(BackendServer server) {
        logger.info("The value of the server and list of servers is :{}", server.getUrl());
        if(servers.containsKey(server.hashCode())) {
            servers.remove(server.hashCode());
        }
        else {
            logger.info("There is no such server :{}", server);
        }
    }
}
