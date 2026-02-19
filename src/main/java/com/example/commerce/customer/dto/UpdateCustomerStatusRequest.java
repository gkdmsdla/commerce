package com.example.commerce.customer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateCustomerStatusRequest {

    @NotBlank
    private String status;
}
