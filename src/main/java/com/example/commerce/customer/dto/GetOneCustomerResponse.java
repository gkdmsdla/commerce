package com.example.commerce.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GetOneCustomerResponse {
    private final Long customerId;
    private final String customerName;
    private final String customerEmail;
    private final String customerPhone;
    private final String customerStatusName;
    private final LocalDateTime customerCreatedAt;
    private final LocalDateTime customerModifiedAt;
}
