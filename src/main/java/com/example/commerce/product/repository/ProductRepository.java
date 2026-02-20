package com.example.commerce.product.repository;

import com.example.commerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByProductnameOrderByCreatedAtDesc(String name);
    List<Product> findAllByOrderByCreatedAtDesc();
}
