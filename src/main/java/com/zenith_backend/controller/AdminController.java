package com.zenith_backend.controller;

import com.zenith_backend.model.Order;
import com.zenith_backend.model.Product;
import com.zenith_backend.service.OrderService;
import com.zenith_backend.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final OrderService orderService;
    private final ProductService productService;

    public AdminController(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(orderService.getDashboardStats());
    }

    // Get all products for the "Inventory Management" section
    @GetMapping("/inventory")
    public ResponseEntity<List<Product>> getFullInventory() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        // This saves the new product directly to your 'product' table
        return ResponseEntity.ok(productService.saveProduct(product));
    }
    // 1. Fetch All Orders
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(orderService.getAdminOrders());
    }

    // 2. Update Order Status
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable int id, @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status");

        if (newStatus == null || newStatus.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Status cannot be empty");
        }

        Order updatedOrder = orderService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok(Map.of(
                "message", "Order status updated successfully",
                "newStatus", updatedOrder.getStatus()
        ));
    }

    // 3. Financial Ledger
    @GetMapping("/finance")
    public ResponseEntity<?> getFinanceLedger() {
        return ResponseEntity.ok(orderService.getFinancialLedger());
    }
}