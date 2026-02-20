package com.example.commerce.order.dto;

import com.example.commerce.order.entity.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetAllCustomerOrderResponse {
    private final long orderNo;
    private final String customerName;
    private final String produtctName;
    private final OrderStatus orderStatus;


    public GetAllCustomerOrderResponse(long orderNo, String customerName, String produtctName, OrderStatus orderStatus) {
        this.orderNo = orderNo;
        this.customerName = customerName;
        this.produtctName = produtctName;
        this.orderStatus = orderStatus;
    }
}