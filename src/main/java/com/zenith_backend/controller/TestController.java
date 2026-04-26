package com.zenith_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private boolean isAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute("userRole");
        return "ADMIN".equals(role);
    }
    @GetMapping("/")
    public String home() {
        return "Zenith backend is running 🚀";
    }

    @GetMapping("/api/test")
    public String test() {
        return "API is working!";
    }
}