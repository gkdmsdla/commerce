package com.example.commerce.order.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PREPARING("준비"),
    SHIPPING("배송"),
    DELIVERED("배송 완료"),
    CANCELED("취소됨");

    private final String description;

    OrderStatus(String description){
        this.description = description;
    }

}
