package com.zenith_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;

    private double totalAmount;
    private String status;
    private String paymentId;
    private String paymentStatus;
    private String paymentMethod;
    private String address;
    private String city;
    private String zip;
    private java.time.LocalDateTime orderDate;


    public Order() {}

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public double getTotalAmount() { return totalAmount; }

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }

    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

public String getAddress() { return address; }
public void setAddress(String address) { this.address = address; }

public String getCity() { return city; }
public void setCity(String city) { this.city = city; }

public String getZip() { return zip; }
public void setZip(String zip) { this.zip = zip; }
    public java.time.LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(java.time.LocalDateTime orderDate) { this.orderDate = orderDate; }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "orderId")
    private java.util.List<OrderItem> items;


    public java.util.List<OrderItem> getItems() { return items; }
    public void setItems(java.util.List<OrderItem> items) { this.items = items; }


}
