package com.example.loadbalancer.strategy;

import com.example.loadbalancer.model.BackendServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RandomStrategyTest {

    private RandomStrategy randomStrategy;
    private ConcurrentHashMap<Integer, BackendServer> servers;

    @BeforeEach
    void setUp() {
        randomStrategy = new RandomStrategy();
        servers = new ConcurrentHashMap<>();
        servers.put(1, new BackendServer("http://localhost:8081"));
        servers.put(2, new BackendServer("http://localhost:8082"));
        servers.put(3, new BackendServer("http://localhost:8083"));
    }

    @Test
    void testGetServer() {
        BackendServer server = randomStrategy.getServer(servers);

        assertNotNull(server);
        assert(servers.containsValue(server));
    }

    @Test
    void testGetServerWithMock() {
        RandomStrategy mockedRandomStrategy = mock(RandomStrategy.class);
        when(mockedRandomStrategy.getServer(servers)).thenReturn(servers.get(1));

        BackendServer server = mockedRandomStrategy.getServer(servers);

        assertNotNull(server);
        assert(server.getUrl().equals("http://localhost:8081"));
    }
}
