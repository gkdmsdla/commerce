package com.example.commerce.customer.entity;

import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomerStatus {

    ACTIVE("활성"),
    INACTIVE("비활성"),
    SUSPENDED("정지");

    private final String statusName;

//    CustomerStatus(String statusName) {
//        this.statusName = statusName;
//    }

    // String to Enum
    // 변환 못하면 throw Invalid Input value
    public static CustomerStatus from(String statusName){
        // 입력받은 상태명이 공백
        if(statusName == null || statusName.isBlank()){
            throw new ServiceException(ErrorCode.INVALID_STATUS);
        }

        for (CustomerStatus s: CustomerStatus.values()){
            if(CustomerStatus.valueOf(statusName) == s){
                return s;
            }
        }

        throw new ServiceException(ErrorCode.INVALID_STATUS);
    }
}
