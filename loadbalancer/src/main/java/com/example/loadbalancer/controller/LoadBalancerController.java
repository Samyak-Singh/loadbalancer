package com.example.loadbalancer.controller;

import com.example.loadbalancer.model.BackendServer;
import com.example.loadbalancer.service.LoadBalancerService;
import com.example.loadbalancer.strategy.RandomStrategy;
import com.example.loadbalancer.strategy.RoundRobinStrategy;
import com.example.loadbalancer.util.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.example.loadbalancer.util.Constants;

import static com.example.loadbalancer.util.Constants.RANDOM;
import static com.example.loadbalancer.util.Constants.ROUND_ROBIN;

@RestController
@RequestMapping("/loadbalancer")
public class LoadBalancerController {
    private final LoadBalancerService loadBalancerService;
    private final RestTemplate restTemplate;
    private final ExponentialBackoffRetry<String> exponentialBackoffRetry;
    private static final Logger logger = LoggerFactory.getLogger(LoadBalancerController.class);

    @Autowired
    public LoadBalancerController(LoadBalancerService loadBalancerService, RestTemplate restTemplate,
                                  ExponentialBackoffRetry<String> exponentialBackoffRetry) {
        this.loadBalancerService = loadBalancerService;
        this.restTemplate = restTemplate;
        this.exponentialBackoffRetry = exponentialBackoffRetry;
    }

    @GetMapping("/forward")
    public ResponseEntity<String> forwardRequest() {
        BackendServer server = loadBalancerService.getNextServer();
        String url = server.getUrl() + "/data";
        try {
            String response = exponentialBackoffRetry.executeWithRetry(() -> {
                logger.info("Trying to forward request to {}", url);
                return restTemplate.getForObject(url, String.class);
            });
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to forward request to backend server after retries: {}", e.getMessage());
            return ResponseEntity.status(500).body("Failed to forward request to backend server");
        }
    }

    @PostMapping("/addServer")
    public ResponseEntity<Void> addServer(@RequestBody BackendServer server) {
        logger.info("Received request to add server: {}", server.getUrl());
        loadBalancerService.addServer(server);
        logger.info("Server added successfully: {}", server.getUrl());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/removeServer")
    public ResponseEntity<Void> removeServer(@RequestBody BackendServer server) {
        logger.info("Received request to remove server: {}", server.getUrl());
        loadBalancerService.removeServer(server);
        logger.info("Server removed successfully: {}", server.getUrl());
        return ResponseEntity.ok().build();
    }
    @PostMapping("/setStrategy")
    public ResponseEntity<Void> setStrategy(@RequestParam(name = "strategy") String strategy) {
        switch (strategy.toLowerCase()) {
            case ROUND_ROBIN:
                loadBalancerService.setStrategy(new RoundRobinStrategy());
                break;
            case RANDOM:
                loadBalancerService.setStrategy(new RandomStrategy());
                break;
            default:
                return ResponseEntity.badRequest().build();
        }
        logger.info("Load balancing strategy set to: {}", strategy);
        return ResponseEntity.ok().build();
    }
}
