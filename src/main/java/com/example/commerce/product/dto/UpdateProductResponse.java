package com.example.commerce.product.dto;


import java.time.LocalDateTime;

public record UpdateProductResponse(
        Long productId,
        String productName,
        String categoryName,
        int productPrice,
        String statusName,
        LocalDateTime modifiedAt
) {}
