package com.example.commerce.product.entity;

import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStatus {
    AVAILABLE("판매중"),
    SOLD_OUT("품절"),
    DISCONTINUED("단종");

    private final String statusName;

    public static ProductStatus from(String status) {
        // 입력받은 직책이 공백
        if (status == null || status.isBlank()) {
            throw new ServiceException(ErrorCode.INVALID_STATUS);
            // 잘못 입력했다고 생각하고 잘못된 입력값 return
        }

        // 직책 찾기
        for (ProductStatus r : ProductStatus.values()) {
            if (ProductStatus.valueOf(status) == r) {
                return r;
            }
        }

        throw new ServiceException(ErrorCode.INVALID_STATUS);
    }

}


