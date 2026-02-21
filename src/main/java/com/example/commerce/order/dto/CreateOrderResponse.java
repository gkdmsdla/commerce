package com.example.commerce.order.dto;

import com.example.commerce.order.entity.OrderStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CreateOrderResponse {
    // 고객이 주문에 성공했을때 반환되는 정보

    private final long orderId;
    private final long orderNo;
    private final String productName;
    private final int productPrice;
    private final int quantity;
    private final long totalPrice;
    private final OrderStatus orderStatus;
    private final LocalDateTime createdAt;
}
