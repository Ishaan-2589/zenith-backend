package com.zenith_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int userId;

    private int productId;
    private int quantity;

    public CartItem() {}

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }

    public void setId(int id) { this.id = id; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}