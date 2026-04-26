package com.zenith_backend.repository;

import com.zenith_backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(int userId);

    List<Order> findByPaymentStatusIgnoreCase(String paymentStatus);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.paymentStatus = 'Paid'")
    Double getTotalRevenue();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'Processing'")
    long countPendingOrders();
}