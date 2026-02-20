package com.example.commerce.order.dto;


import com.example.commerce.order.entity.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetAllAdminOrderRequest {
    private long orderNo;

    private String customerName;

    private String productName;
    private OrderStatus orderStatus;

    private String adminName;
}
