package com.example.commerce.order.repository;

import com.example.commerce.customer.entity.Customer;
import com.example.commerce.order.entity.Order;
import com.example.commerce.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long>{
    @Query("SELECT o FROM Order o WHERE " +
            "(:customer IS NULL OR :customer = o.customer) AND " +
            "(:keyword IS NULL OR o.customer.name LIKE %:keyword% OR CAST(o.orderNo AS string) LIKE %:keyword%) AND " +
            "(:status IS NULL OR o.orderStatus = :status)")
    Page<Order> searchOrders(
            @Param("keyword") String keyword,
            @Param("status") OrderStatus status,
            @Param("customer") Customer customer,
            Pageable pageable);

}