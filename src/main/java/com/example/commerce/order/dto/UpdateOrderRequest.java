package com.example.commerce.order.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UpdateOrderRequest {

    private Long orderNo;
    private Enum orderStatus;
    private Long customersId;
    private Long AdminId;
    private String ProductId;
    private LocalDateTime CreatedAt;
    private LocalDateTime ModifiedAt;
}
