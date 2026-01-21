package com.align.shophub.repository;

import com.align.shophub.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // ... existing search method ...
    @Query("SELECT p FROM Product p WHERE " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Product> searchProducts(
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("search") String search
    );

    long countByTotalStockLessThan(int stockLimit);

    // Analytics: Sales by Category
    @Query("SELECT p.category, COUNT(oi) FROM OrderItem oi JOIN oi.product p GROUP BY p.category")
    List<Object[]> findSalesByCategory();

    List<Product> findByCategory(String category);
}