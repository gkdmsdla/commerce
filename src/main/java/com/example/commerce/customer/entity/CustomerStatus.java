package com.example.commerce.customer.entity;

import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
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

    public static CustomerStatus from(String statusName){
        // 입력받은 상태명이 공백
        if(statusName == null || statusName.isBlank()){
            throw new ServiceException(ErrorCode.INVALID_INPUT_VALUE);
        }

        for (CustomerStatus s: CustomerStatus.values()){
            if(CustomerStatus.valueOf(statusName) == s){
                return s;
            }
        }

        throw new ServiceException(ErrorCode.INVALID_INPUT_VALUE);
    }
}
