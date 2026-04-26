package com.zenith_backend.repository;

import com.zenith_backend.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Integer> {

    List<CartItem> findByUserId(int userId);
    Optional<CartItem> findByUserIdAndProductId(int userId, int productId);

}