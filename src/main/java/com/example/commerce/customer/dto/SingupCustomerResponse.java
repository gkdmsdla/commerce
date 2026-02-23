package com.example.commerce.customer.dto;

import lombok.RequiredArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class SingupCustomerResponse {

    private final Long customerId;
    private final String customerName;
    private final String customerEmail;
    private final String customerPhone;
    private final String customerStatus;
    private final LocalDateTime customerCreatedAt;
}
