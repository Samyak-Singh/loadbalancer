package com.example.loadbalancer.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class ExponentialBackoffRetry<T> {

    private final int maxRetries;
    private final long initialDelay;
    private final double multiplier;

    public ExponentialBackoffRetry(@Value("${retry.maxRetries}") int maxRetries,
                                   @Value("${retry.initialDelay}") long initialDelay,
                                   @Value("${retry.multiplier}") double multiplier) {
        this.maxRetries = maxRetries;
        this.initialDelay = initialDelay;
        this.multiplier = multiplier;
    }

    public T executeWithRetry(Supplier<T> action) throws Exception {
        int attempt = 0;
        long delay = initialDelay;
        while (true) {
            try {
                return action.get();
            } catch (Exception e) {
                if (attempt >= maxRetries) {
                    throw new Exception("Max retry attempts reached", e);
                }
                attempt++;
                Thread.sleep(delay);
                delay *= multiplier;
            }
        }
    }
}
