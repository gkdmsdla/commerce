package com.example.commerce.order.dto;

import com.example.commerce.admin.entity.Role;
import com.example.commerce.order.entity.OrderStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CreateAdminOrderResponse {

    private final long orderId;
    private final long orderNo;

    private final long customerId;
    private final String customerName;

    private final String productName;
    private final int productPrice;
    private final int quantity;
    private final long totalPrice;
    private final OrderStatus orderStatus;
    private final LocalDateTime createdAt;

    private final String adminName; //주문 등록 관리자명
    private final String adminEmail;
    private final Role adminRole;
}
