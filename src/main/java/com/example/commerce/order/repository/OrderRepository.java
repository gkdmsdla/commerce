package com.example.commerce.order.repository;

import com.example.commerce.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByUsername(String username);
    // 중복 체크
}
