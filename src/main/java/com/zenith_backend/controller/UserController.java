package com.zenith_backend.controller;

import com.zenith_backend.model.User;
import com.zenith_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;

    private boolean isAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute("userRole");
        return "ADMIN".equals(role);
    }

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/users")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping("/api/users")
    public User createUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PostMapping("/api/login")
    public Map<String, String> login(@RequestBody User user) {
        String token = userService.loginUser(user.getEmail(), user.getPassword());
        return Map.of("token", token);
    }

    @GetMapping("/api/protected")
    public String protectedRoute() {
        return "You accessed a protected route 🔐";
    }

    @GetMapping("/api/users/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");

        if (email == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return ResponseEntity.ok(userService.getUserProfile(email));
    }

    @PutMapping("/api/users/me")
    public ResponseEntity<?> updateProfile(HttpServletRequest request, @RequestBody Map<String, String> updates) {
        String email = (String) request.getAttribute("userEmail");

        if (email == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return ResponseEntity.ok(userService.updateUserProfile(email, updates));
    }

}