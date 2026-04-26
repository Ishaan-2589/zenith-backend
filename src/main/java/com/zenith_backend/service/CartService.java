package com.zenith_backend.service;

import com.zenith_backend.model.CartItem;
import com.zenith_backend.model.Product;
import com.zenith_backend.model.User;
import com.zenith_backend.repository.CartRepository;
import com.zenith_backend.repository.ProductRepository;
import com.zenith_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartService {

    private final CartRepository repo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public CartService(CartRepository repo, ProductRepository productRepo, UserRepository userRepo) {
        this.repo = repo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    public List<Map<String, Object>> getCartByUser(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> items = repo.findByUserId(user.getId());

        List<Map<String, Object>> result = new ArrayList<>();

        for (CartItem item : items) {
            Product p = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("name", p.getName());
            map.put("price", p.getPrice());
            map.put("quantity", item.getQuantity());
            map.put("productId", p.getId());
            map.put("imageUrl", p.getImageUrl());

            result.add(map);
        }

        return result;
    }

    public CartItem addToCartWithUser(CartItem item, String email) {
        if (item.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepo.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int userId = user.getId();
        Optional<CartItem> existingItem = repo.findByUserIdAndProductId(userId, item.getProductId());

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + item.getQuantity();

            if (newQuantity > product.getStock()) {
                throw new RuntimeException("Cannot add more. Only " + product.getStock() + " items in stock.");
            }

            cartItem.setQuantity(newQuantity);
            return repo.save(cartItem);
        } else {
            if (item.getQuantity() > product.getStock()) {
                throw new RuntimeException("Cannot add. Only " + product.getStock() + " items in stock.");
            }
            item.setUserId(userId);
            return repo.save(item);
        }
    }

    public void removeItem(int id, String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CartItem item = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (item.getUserId() != user.getId()) {
            throw new RuntimeException("Unauthorized action");
        }

        repo.delete(item);
    }
}