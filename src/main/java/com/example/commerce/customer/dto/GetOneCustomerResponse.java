package com.example.commerce.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GetOneCustomerResponse {
    private final Long id;
    private final String name;
    private final String email;
    private final String phone;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
}
