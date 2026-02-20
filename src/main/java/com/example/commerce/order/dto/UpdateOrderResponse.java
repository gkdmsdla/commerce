package com.example.commerce.order.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UpdateOrderResponse {

    private final Long orderNo;
    private final Enum orderStatus;
    private final Long customersId;
    private final Long AdminId;
    private final String ProductId;
    private final LocalDateTime CreatedAt;
    private final LocalDateTime ModifiedAt;

    public UpdateOrderResponse(Long orderNo, Enum orderStatus, Long customersId, Long adminId, String productId, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.orderNo = orderNo;
        this.orderStatus = orderStatus;
        this.customersId = customersId;
        this.AdminId = adminId;
        this.ProductId = productId;
        this.CreatedAt = createdAt;
        this.ModifiedAt = modifiedAt;
    }
}
