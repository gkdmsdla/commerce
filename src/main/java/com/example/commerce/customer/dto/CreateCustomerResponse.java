package com.example.commerce.customer.dto;

import com.example.commerce.customer.entity.CustomerStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CreateCustomerResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final String phone;
    private final CustomerStatus status;
    private final String statusName;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
}
