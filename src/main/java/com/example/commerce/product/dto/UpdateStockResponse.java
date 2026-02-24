package com.example.commerce.product.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class UpdateStockResponse {
    private final Long productId;
    private final String productName;
    private final int previousStock;
    private final int newStock;
    private final LocalDateTime modifiedAt;
}
