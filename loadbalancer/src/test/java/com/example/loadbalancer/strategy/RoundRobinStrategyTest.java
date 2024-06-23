package com.example.loadbalancer.strategy;

import com.example.loadbalancer.model.BackendServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoundRobinStrategyTest {

    private RoundRobinStrategy roundRobinStrategy;
    private ConcurrentHashMap<Integer, BackendServer> servers;

    @BeforeEach
    void setUp() {
        roundRobinStrategy = new RoundRobinStrategy();
        servers = new ConcurrentHashMap<>();
        servers.put(1, new BackendServer("http://localhost:8081"));
        servers.put(2, new BackendServer("http://localhost:8082"));
        servers.put(3, new BackendServer("http://localhost:8083"));
    }

    @Test
    void testGetServer() {
        BackendServer server1 = roundRobinStrategy.getServer(servers);
        BackendServer server2 = roundRobinStrategy.getServer(servers);
        BackendServer server3 = roundRobinStrategy.getServer(servers);
        BackendServer server4 = roundRobinStrategy.getServer(servers);

        assertEquals("http://localhost:8081", server1.getUrl());
        assertEquals("http://localhost:8082", server2.getUrl());
        assertEquals("http://localhost:8083", server3.getUrl());
        assertEquals("http://localhost:8081", server4.getUrl());
    }

    @Test
    void testRoundRobinResetsIndex() {
        for (int i = 0; i < 6; i++) {
            roundRobinStrategy.getServer(servers);
        }

        BackendServer server = roundRobinStrategy.getServer(servers);
        assertEquals("http://localhost:8081", server.getUrl());
    }
}
