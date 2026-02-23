package com.example.commerce.customer.dto;

public record LoginCustomerResponse(
        String token,               // 발급된 JWT 토큰
        Long customerId,
        String customerName,
        String customerEmail,
        String customerStatusName
) {}
