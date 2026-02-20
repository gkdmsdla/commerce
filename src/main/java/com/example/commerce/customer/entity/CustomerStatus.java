package com.example.commerce.customer.entity;

import lombok.Getter;

@Getter
public enum CustomerStatus {

    ACTIVE("활성"),
    INACTIVE("비활성"),
    SUSPENDED("정지");

    private final String statusName;

    CustomerStatus(String statusName) {
        this.statusName = statusName;
    }
}
