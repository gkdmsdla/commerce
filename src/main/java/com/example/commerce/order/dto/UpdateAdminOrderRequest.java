package com.example.commerce.order.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UpdateAdminOrderRequest {

    //Long -> id, UUID -> orderNo
    private Long orderNo;
    private String statusName;
    private Long customersId;
    private Long AdminId;
    private String ProductId;
    private LocalDateTime CreatedAt;
    private LocalDateTime ModifiedAt;
}
