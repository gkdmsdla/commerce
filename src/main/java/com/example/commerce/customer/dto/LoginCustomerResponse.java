package com.example.commerce.customer.dto;

public record LoginCustomerResponse(
        Long customerId,
        String customerName,
        String customerEmail,
        String customerStatusName
) {}
