package com.zenith_backend.controller;

import com.zenith_backend.model.CartItem;
import com.zenith_backend.repository.UserRepository;
import com.zenith_backend.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService service;
    private final UserRepository userRepo;

    private boolean isAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute("userRole");
        return "ADMIN".equals(role);
    }

    public CartController(CartService service, UserRepository userRepo) {
        this.service = service;
        this.userRepo = userRepo;
    }

    @PostMapping
    public CartItem add(@RequestBody CartItem item, HttpServletRequest request) {

        String email = (String) request.getAttribute("userEmail");

        if (email == null) {
            throw new RuntimeException("Unauthorized");
        }

        return service.addToCartWithUser(item, email);
    }

    @GetMapping
    public ResponseEntity<?> getCart(HttpServletRequest request) {
        // 1. Get the email securely from the JWT token
        String email = (String) request.getAttribute("userEmail");

        if (email == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // 2. Just pass the email directly to the service!
        // The CartService handles finding the User internally.
        return ResponseEntity.ok(service.getCartByUser(email));
    }

    @DeleteMapping("/{id}")
    public String remove(@PathVariable int id, HttpServletRequest request) {

        String email = (String) request.getAttribute("userEmail");

        if (email == null) {
            throw new RuntimeException("Unauthorized");
        }

        service.removeItem(id, email);

        return "Item removed";
    }
}