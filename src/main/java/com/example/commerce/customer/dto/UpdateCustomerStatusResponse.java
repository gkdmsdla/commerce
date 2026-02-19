package com.example.commerce.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UpdateCustomerStatusResponse {
    private final Long id;
    private final String status;
    private final LocalDateTime modifiedAt;
}
