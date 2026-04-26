package com.zenith_backend.controller;

import com.zenith_backend.model.Product;
import com.zenith_backend.service.ImageService;
import com.zenith_backend.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;
    private final ImageService imageService;

    public ProductController(ProductService service, ImageService imageService) {
        this.service = service;
        this.imageService = imageService;
    }

    @GetMapping
    public List<Product> getProducts() {
        return service.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable int id) {
        return service.getProductById(id);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addProduct(
            @RequestPart("product") Product product,
            @RequestPart("image") MultipartFile imageFile) throws IOException {

        // 1. Upload the image to Cloudinary
        String imageUrl = imageService.uploadImage(imageFile);

        // 2. Set the URL in the product object
        product.setImageUrl(imageUrl);

        // 3. Save to database
        service.addProduct(product);

        return ResponseEntity.ok("Product added with Cloudinary image!");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable int id, @RequestBody Product product) {
        service.updateProduct(id, product);
        return ResponseEntity.ok("Updated");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) { // Use int
        service.deleteProduct(id);
        return ResponseEntity.ok("Deleted");
    }
    @GetMapping("/search")
    public List<Product> search(@RequestParam String query) {
        return service.searchProducts(query);
    }
}