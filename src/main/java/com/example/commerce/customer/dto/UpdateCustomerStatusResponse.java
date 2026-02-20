package com.example.commerce.customer.dto;

import com.example.commerce.customer.entity.CustomerStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UpdateCustomerStatusResponse {
    private final Long customerId;
    private final String customerStatusName;
    private final LocalDateTime modifiedAt;
}
