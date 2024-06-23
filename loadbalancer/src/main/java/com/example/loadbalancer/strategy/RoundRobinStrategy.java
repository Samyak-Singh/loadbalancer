package com.example.loadbalancer.strategy;

import com.example.loadbalancer.model.BackendServer;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Primary
public class RoundRobinStrategy implements LoadBalancingStrategy {

    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public BackendServer getServer(ConcurrentHashMap<Integer, BackendServer> servers) {
        int currentIndex = Math.abs(index.getAndIncrement() % servers.size());
        return servers.values().toArray(new BackendServer[0])[currentIndex];
    }
}