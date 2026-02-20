package com.example.commerce.order.dto;

import com.example.commerce.order.entity.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetAllAdminOrderResponse {
    private final long orderId;
    private final long orderNo;

    private final String customerName;

    private final String productName;
    private final long totalPrice;
    private final OrderStatus orderStatus;
    private final LocalDateTime createdAt;

    private final String adminName; //주문 등록 관리자명
    private final Role adminRole;

    public GetAllAdminOrderResponse(long orderId, long orderNo, String customerName, String productName, long totalPrice, OrderStatus orderStatus, LocalDateTime createdAt, String adminName, Role adminRole) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.customerName = customerName;
        this.productName = productName;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.adminName = adminName;
        this.adminRole = adminRole;
    }
}