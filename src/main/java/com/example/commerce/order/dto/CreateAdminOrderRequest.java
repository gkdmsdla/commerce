package com.example.commerce.order.dto;

import lombok.Getter;

@Getter
public class CreateAdminOrderRequest {
    private int quantity;

    // 주문자 정보
    private long customerId;

    // 상품 정보
    private long productId;
}