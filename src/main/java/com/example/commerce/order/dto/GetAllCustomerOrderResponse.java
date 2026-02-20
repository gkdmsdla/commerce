package com.example.commerce.order.dto;

import com.example.commerce.order.entity.OrderStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class GetAllCustomerOrderResponse {
    private final long orderNo;
    private final String customerName;
    private final String productName;
    private final OrderStatus orderStatus;
}