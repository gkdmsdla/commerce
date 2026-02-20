package com.example.commerce.product.dto;

import com.example.commerce.product.entity.ProductCategory;
import com.example.commerce.product.entity.ProductStatus;

import java.time.LocalDateTime;

public record GetAllProductResponse (
    Long productId, String productName, ProductCategory productCategory,
    int productPrice, int productStock, ProductStatus productstatus,
    LocalDateTime createdAt) {}
