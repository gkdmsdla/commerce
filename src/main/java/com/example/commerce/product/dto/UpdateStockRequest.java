package com.example.commerce.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateStockRequest {
    @NotBlank(message = "재고는 필수입니다.")
    @Min(0)
    private int stock;
}
