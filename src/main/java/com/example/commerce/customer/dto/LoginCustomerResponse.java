package com.example.commerce.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginCustomerResponse {
    private final Long customerId;
    private final String customerName;
    private final String customerEmail;
    private final String customerStatusName;
}
