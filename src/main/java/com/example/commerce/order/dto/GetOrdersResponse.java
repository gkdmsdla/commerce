package com.example.commerce.order.dto;


import java.time.LocalDateTime;
import java.util.UUID;

public record GetOrdersResponse(
        Long orderId,
        UUID orderNo,
        String customerName,
        String productName,
        String orderStatus,
        int quantity,
        LocalDateTime createdAt
) {
}