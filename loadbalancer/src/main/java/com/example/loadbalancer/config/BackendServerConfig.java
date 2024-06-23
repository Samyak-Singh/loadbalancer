package com.example.loadbalancer.config;

import com.example.loadbalancer.model.BackendServer;
import com.example.loadbalancer.service.LoadBalancerService;
import com.example.loadbalancer.util.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class BackendServerConfig {
    @Bean
    public ConcurrentHashMap<Integer, BackendServer> backendServers() {
        ConcurrentHashMap<Integer, BackendServer> servers = new ConcurrentHashMap<>();
        BackendServer server1 = new BackendServer("http://localhost:8082");
        BackendServer server2 = new BackendServer("http://localhost:8083");
        BackendServer server3 = new BackendServer("http://localhost:8084");
        servers.put(server1.hashCode(), server1);
        servers.put(server2.hashCode(), server2);
        servers.put(server3.hashCode(), server3);

        return servers;
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
