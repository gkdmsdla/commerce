package com.example.commerce.order.dto;

import lombok.Getter;

@Getter
public class GetAllAdminOrderRequest {
    private long orderNo;

    private String customerName;

    private String productName;
    private String statusName;

    private String adminName;
}
