package com.example.commerce.order.dto;


import java.util.UUID;

public record GetAllCustomerOrderResponse(
        UUID orderNo,
        String customerName,
        String productName,
        String orderStatus
) {
}