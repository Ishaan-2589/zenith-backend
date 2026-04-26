package com.zenith_backend.controller;

import com.zenith_backend.model.Order;
import com.zenith_backend.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    private boolean isAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute("userRole");
        return "ADMIN".equals(role);
    }

    @PostMapping
    public Order placeOrder(HttpServletRequest request, @RequestBody Map<String, Object> orderData) {

        String email = (String) request.getAttribute("userEmail");

        if (email == null) {
            throw new RuntimeException("Unauthorized");
        }

        return service.placeOrder(email, orderData);
    }

    @GetMapping
    public List<Map<String, Object>> getOrders(HttpServletRequest request) {

        String email = (String) request.getAttribute("userEmail");

        if (email == null) {
            throw new RuntimeException("Unauthorized");
        }

        return service.getOrdersByUser(email);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable int id,
            @RequestParam String status) {

        service.updateOrderStatus(id, status);
        return ResponseEntity.ok("Order status updated");
    }
}