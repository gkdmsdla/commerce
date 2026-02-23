package com.example.commerce.order.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetAllAdminOrderResponse(
        Long orderId,
        UUID orderNo,

        String customerName,

        String productName,
        long totalPrice,
        String orderStatus,

        LocalDateTime createdAt,

        String adminName //주문 등록 관리자명
) {
}