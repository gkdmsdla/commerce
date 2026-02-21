package com.example.commerce.order.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class GetOneOrderResponse {


    private final long orderNo;
    private final int orderQuantity;
    private final String orderStatus;

    // 고객
    private final String customerName;
    private final String customerEmail;

    //상품
    private final String productName;
    private final int productPrice;

    private final LocalDateTime createdAt;
}
