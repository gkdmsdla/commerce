package com.example.commerce.order.dto;

import com.example.commerce.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateOrderResponse(
        // 고객이 주문에 성공했을때 반환되는 정보

        long orderId,
        UUID orderNo,

        String productName,
        int productPrice,

        int quantity,
        long totalPrice,

        String orderStatusName,

        LocalDateTime createdAt
) { }
