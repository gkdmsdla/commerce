package com.example.commerce.customer.dto;

import java.time.LocalDateTime;

public record CreateCustomerResponse(
        Long customerId,
        String customerName,
        String customerEmail,
        String customerPhone,
        String customerStatusName,
        LocalDateTime customerCreatedAt,
        LocalDateTime customerModifiedAt
) {}
