package com.example.commerce.order.dto;

import lombok.Getter;

@Getter
public class CreateAdminOrderRequest {
    // 관리자는 주문자와 상품 ID 자유롭게 확인할 수 있음을 가정함.
    private int quantity;

    // 주문자 정보
    private long customerId;

    // 상품 정보
    private long productId;
}