package com.example.commerce.order.dto;

import com.example.commerce.admin.entity.Role;
import com.example.commerce.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateAdminOrderResponse(

        long orderId,
        UUID orderNo,

        long customerId,
        String customerName,

        String productName,
        int productPrice,
        int quantity,
        long totalPrice,
        OrderStatus orderStatus,
        LocalDateTime createdAt,

        String adminName, //주문 등록 관리자명
        String adminEmail,
        Role adminRole
) { }

// 똑같은 타입일 경우 들어오는 데이터 순서가 달라져도 결과를 보기 전까지 모름
