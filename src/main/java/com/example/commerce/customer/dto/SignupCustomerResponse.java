package com.example.commerce.customer.dto;

import java.time.LocalDateTime;

public record SignupCustomerResponse(
        Long customerId,
        String customerName,
        String customerEmail,
        String customerPhone,
        String customerStatus,
        LocalDateTime customerCreatedAt
) {}
