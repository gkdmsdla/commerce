package com.example.commerce.customer.dto;

import java.time.LocalDateTime;

public record UpdateCustomerStatusResponse(Long id, String statusName, LocalDateTime modifiedAt) {
}
