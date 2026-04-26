package com.zenith_backend.repository;

import com.zenith_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Finds products where name or description contains the keyword (case-insensitive)
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    Optional<Product> findByName(String name);
}