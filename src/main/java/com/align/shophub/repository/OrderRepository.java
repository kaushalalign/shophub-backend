package com.align.shophub.repository;

import com.align.shophub.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(int userId);

    // Total Revenue
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.paymentStatus = 'PAID'")
    BigDecimal calculateTotalRevenue();

    // Analytics: Monthly Sales (Simple grouping by date)
    @Query(value = "SELECT DATE(order_date) as date, COUNT(*) as count, SUM(total_amount) as total " +
            "FROM orders GROUP BY DATE(order_date) ORDER BY DATE(order_date) DESC LIMIT 30",
            nativeQuery = true)
    List<Object[]> findDailySalesStats();
}