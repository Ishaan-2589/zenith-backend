package com.zenith_backend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int orderId;
    private int productId;
    private int quantity;
    private double price;

    public OrderItem() {}

    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }

    public void setId(int id) { this.id = id; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }


    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "productId", insertable = false, updatable = false)
    private Product product;

    public Product getProduct() { return product; }
}