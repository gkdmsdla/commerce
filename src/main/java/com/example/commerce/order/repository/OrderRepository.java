package com.example.commerce.order.repository;

import com.example.commerce.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByOrderNo(UUID orderNo);
    // 중복 체크
}
