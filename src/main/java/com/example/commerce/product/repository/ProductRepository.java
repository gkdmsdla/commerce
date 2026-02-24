package com.example.commerce.product.repository;

import com.example.commerce.product.entity.Category;
import com.example.commerce.product.entity.Product;
import com.example.commerce.product.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface ProductRepository extends JpaRepository<Product, Long> {
//    List<Product> findAllByProductnameOrderByCreatedAtDesc(String name);
//    List<Product> findAllByOrderByCreatedAtDesc();

    @Query("SELECT p FROM Product p WHERE " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:keyword IS NULL OR p.name LIKE %:keyword%) AND " +
            "(:status IS NULL OR p.status = :status)")
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("category") Category category,
            @Param("status") ProductStatus status,
            Pageable pageable
    );
}
