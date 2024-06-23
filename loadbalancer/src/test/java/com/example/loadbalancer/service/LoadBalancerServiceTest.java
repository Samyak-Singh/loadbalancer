package com.example.loadbalancer.service;

import com.example.loadbalancer.model.BackendServer;
import com.example.loadbalancer.strategy.LoadBalancingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoadBalancerServiceTest {

    @Mock
    private LoadBalancingStrategy loadBalancingStrategy;

    @InjectMocks
    private LoadBalancerService loadBalancerService;

    private ConcurrentHashMap<Integer, BackendServer> backendServers;
    private BackendServer server1;
    private BackendServer server2;
    private BackendServer server3;

    @BeforeEach
    void setUp() {
        server1 = new BackendServer("http://localhost:8082");
        server2 = new BackendServer("http://localhost:8083");
        server3 = new BackendServer("http://localhost:8084");
        backendServers = new ConcurrentHashMap<>();
        backendServers.put(server1.hashCode(), server1);
        backendServers.put(server2.hashCode(), server2);
        backendServers.put(server3.hashCode(), server3);

        loadBalancerService = new LoadBalancerService(backendServers, loadBalancingStrategy);
    }

    @Test
    void testGetNextServer() {
        when(loadBalancingStrategy.getServer(backendServers)).thenReturn(server1);

        BackendServer result = loadBalancerService.getNextServer();

        assertEquals(server1, result);
        verify(loadBalancingStrategy, times(1)).getServer(backendServers);
    }

    @Test
    void testGetNextServerThrowsException() {
        when(loadBalancingStrategy.getServer(backendServers)).thenThrow(new RuntimeException("Selection error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> loadBalancerService.getNextServer());

        assertEquals("Failed to select backend server", exception.getMessage());
        verify(loadBalancingStrategy, times(1)).getServer(backendServers);
    }

    @Test
    void testAddServer() {
        BackendServer newServer = new BackendServer("http://localhost:8085");

        loadBalancerService.addServer(newServer);

        assertTrue(backendServers.containsKey(newServer.hashCode()));
        assertEquals(newServer, backendServers.get(newServer.hashCode()));
    }

    @Test
    void testRemoveServer() {
        loadBalancerService.removeServer(server2);

        assertFalse(backendServers.containsKey(server2.hashCode()));
    }

    @Test
    void testRemoveNonExistentServer() {
        BackendServer nonExistentServer = new BackendServer("http://localhost:8086");

        loadBalancerService.removeServer(nonExistentServer);

        assertFalse(backendServers.containsKey(nonExistentServer.hashCode()));
    }

    @Test
    void testSetStrategy() {
        LoadBalancingStrategy newStrategy = mock(LoadBalancingStrategy.class);
        loadBalancerService.setStrategy(newStrategy);

        when(newStrategy.getServer(backendServers)).thenReturn(server3);
        BackendServer result = loadBalancerService.getNextServer();

        assertEquals(server3, result);
        verify(newStrategy, times(1)).getServer(backendServers);
    }
}
