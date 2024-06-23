package com.example.server2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockController {

    @GetMapping("/data")
    public String getData() {
        return "Response from Mock Server 2";
    }
}
