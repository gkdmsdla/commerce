package com.example.commerce.order.dto;

import lombok.Getter;

@Getter
public class CreateOrderRequest {
    // 고객이 주문 하면서 보내줘야하는 정보
    private long productId;

    private int quantity;
}
