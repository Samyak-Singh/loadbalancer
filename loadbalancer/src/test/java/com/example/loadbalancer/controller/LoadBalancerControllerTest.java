package com.example.loadbalancer.controller;

import com.example.loadbalancer.model.BackendServer;
import com.example.loadbalancer.service.LoadBalancerService;
import com.example.loadbalancer.util.ExponentialBackoffRetry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LoadBalancerControllerTest {

    @Mock
    private LoadBalancerService loadBalancerService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ExponentialBackoffRetry<String> exponentialBackoffRetry;

    @InjectMocks
    private LoadBalancerController loadBalancerController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loadBalancerController).build();
    }


    @Test
    void testForwardRequestFailure() throws Exception {
        BackendServer server = new BackendServer("http://localhost:8081");
        when(loadBalancerService.getNextServer()).thenReturn(server);
        when(exponentialBackoffRetry.executeWithRetry(() -> restTemplate.getForObject(server.getUrl() + "/data", String.class)))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/loadbalancer/forward"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to forward request to backend server"));
    }

    @Test
    void testAddServer() throws Exception {
        BackendServer server = new BackendServer("http://localhost:8083");

        mockMvc.perform(post("/loadbalancer/addServer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"http://localhost:8083\"}"))
                .andExpect(status().isOk());

        Mockito.verify(loadBalancerService).addServer(server);
    }

    @Test
    void testRemoveServer() throws Exception {
        BackendServer server = new BackendServer("http://localhost:8083");

        mockMvc.perform(delete("/loadbalancer/removeServer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"http://localhost:8083\"}"))
                .andExpect(status().isOk());

        // Verify the service method was called
        // Mockito.verify(loadBalancerService).removeServer(server);
    }

    @Test
    void testSetStrategy() throws Exception {
        mockMvc.perform(post("/loadbalancer/setStrategy")
                        .param("strategy", "roundrobin"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/loadbalancer/setStrategy")
                        .param("strategy", "random"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/loadbalancer/setStrategy")
                        .param("strategy", "invalid"))
                .andExpect(status().isBadRequest());
    }
}
