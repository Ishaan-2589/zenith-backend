package com.zenith_backend.service;

import com.zenith_backend.model.User;
import com.zenith_backend.repository.UserRepository;
import com.zenith_backend.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User addUser(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("An account with this email already exists.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole("USER");
        User savedUser = userRepository.save(user);

        String htmlTemplate = """
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #eee; padding: 20px;">
            <div style="text-align: center; background-color: #333; padding: 10px;">
                <h1 style="color: white; margin: 0;">ZENITH</h1>
            </div>
            <div style="padding: 20px;">
                <h2 style="color: #333;">Welcome to the Zenith Family, %s!</h2>
                <p>We're thrilled to have you with us. Explore the latest trends and exclusive offers curated just for you.</p>
                <div style="text-align: center; margin: 30px 0;">
                    <a href="http://localhost:3000" style="background-color: #000; color: #fff; padding: 12px 25px; text-decoration: none; border-radius: 5px;">Start Shopping</a>
                </div>
                <p style="font-size: 12px; color: #777;">If you didn't create this account, please ignore this email.</p>
            </div>
        </div>
        """.formatted(savedUser.getName());

        try {
            emailService.sendHtmlEmail(savedUser.getEmail(), "Welcome to Zenith!", htmlTemplate);
        } catch (Exception e) {
            System.out.println("Email failed: " + e.getMessage());
        }

        return savedUser;
    }

    public String loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Pass the actual user role from the database
        return JwtUtil.generateToken(email, user.getRole());
    }

    public User getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(null);
        return user;
    }

    public User updateUserProfile(String email, Map<String, String> updates) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updates.containsKey("name")) {
            user.setName(updates.get("name"));
        }
        if (updates.containsKey("phone")) {
            user.setPhone(updates.get("phone"));
        }

        userRepository.save(user);
        user.setPassword(null);
        return user;
    }
}