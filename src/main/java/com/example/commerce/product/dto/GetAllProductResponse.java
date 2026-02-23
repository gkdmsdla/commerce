package com.example.commerce.product.dto;
import java.time.LocalDateTime;

public record GetAllProductResponse (
        Long productId,
        String productName,
        String categoryName,
        int productPrice,
        int productStock,
        String statusName,
        LocalDateTime createdAt
) {}
