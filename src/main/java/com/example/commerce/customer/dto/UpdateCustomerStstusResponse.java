package com.example.commerce.customer.dto;

import lombok.Getter;

import java.time.LocalDateTime;

public record UpdateCustomerStstusResponse(Long id, String statusName, LocalDateTime modifiedAt) {
}
