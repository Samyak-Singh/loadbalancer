package com.example.loadbalancer.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ExponentialBackoffRetryTest {

    private ExponentialBackoffRetry<String> exponentialBackoffRetry;

    @BeforeEach
    void setUp() {
        int maxRetries = 3;
        long initialDelay = 100;  // 100 milliseconds
        double multiplier = 2.0;
        exponentialBackoffRetry = new ExponentialBackoffRetry<>(maxRetries, initialDelay, multiplier);
    }

    @Test
    void testExecuteWithRetrySuccess() throws Exception {
        Supplier<String> action = () -> "Success";

        String result = exponentialBackoffRetry.executeWithRetry(action);

        assertEquals("Success", result);
    }

    @Test
    void testExecuteWithRetryPartialSuccess() throws Exception {
        AtomicInteger attemptCounter = new AtomicInteger(0);
        Supplier<String> action = () -> {
            if (attemptCounter.incrementAndGet() < 3) {
                throw new RuntimeException("Failure");
            }
            return "Success";
        };

        String result = exponentialBackoffRetry.executeWithRetry(action);

        assertEquals("Success", result);
        assertEquals(3, attemptCounter.get());
    }
}
