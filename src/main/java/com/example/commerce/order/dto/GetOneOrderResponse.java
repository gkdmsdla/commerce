package com.example.commerce.order.dto;

import org.hibernate.validator.constraints.UUID;

import java.time.LocalDateTime;


public record GetOneOrderResponse(


        UUID orderNo,
        int orderQuantity,
        String orderStatus,

        // 고객
        String customerName,
        String customerEmail,

        //상품
        String productName,
        int productPrice,

        LocalDateTime createdAt
) {
}
