package com.example.loadbalancer.strategy;

import com.example.loadbalancer.model.BackendServer;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public interface LoadBalancingStrategy {
    BackendServer getServer(ConcurrentHashMap<Integer, BackendServer>servers);
}