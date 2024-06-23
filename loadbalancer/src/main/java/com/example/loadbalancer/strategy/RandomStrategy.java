package com.example.loadbalancer.strategy;

import com.example.loadbalancer.model.BackendServer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RandomStrategy implements LoadBalancingStrategy {

    private final Random random = new Random();

    @Override
    public BackendServer getServer(ConcurrentHashMap<Integer, BackendServer> servers) {
        int randomIndex = random.nextInt(servers.size());
        return servers.values().toArray(new BackendServer[0])[randomIndex];
    }
}