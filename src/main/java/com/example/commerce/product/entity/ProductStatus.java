package com.example.commerce.product.entity;

import lombok.Getter;

@Getter
public enum ProductStatus {
    AVAILABLE("판매중"),
    SOLD_OUT("재고 없음"),
    DISCONTINUED("단종");

    private final String statusName;

    ProductStatus(String statusName){
        this.statusName = statusName;
    }

}


