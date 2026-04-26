package com.zenith_backend.service;

import com.zenith_backend.model.Product;
import com.zenith_backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public Product addProduct(Product product) {
        return repo.save(product);
    }

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Product updateProduct(int id, Product updated) {
        Product product = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        product.setName(updated.getName());
        product.setDescription(updated.getDescription());
        product.setPrice(updated.getPrice());
        product.setImageUrl(updated.getImageUrl());
        product.setStock(updated.getStock());

        return repo.save(product);
    }

    public void deleteProduct(int id) {
        repo.deleteById(id);
    }

    public Product getProductById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    public void decreaseStock(int productId, int quantity) {
        Product product = getProductById(productId);
        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
        product.setStock(product.getStock() - quantity);
        repo.save(product);
    }
    public List<Product> searchProducts(String query) {
        return repo.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

    public Product saveProduct(Product product) {

        if (repo.findByName(product.getName()).isPresent()) {
            throw new RuntimeException("A product with this name already exists in the catalog.");
        }
        product.setActive(true);

        return repo.save(product);
    }
}