package com.example.commerce.order.entity;

import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
import lombok.Getter;

@Getter
public enum OrderStatus {
    PREPARING("준비"),
    SHIPPING("배송"),
    DELIVERED("배송 완료"),
    CANCELED("취소됨");

    private final String statusName;

    OrderStatus(String statusName){
        this.statusName = statusName;
    }


    public static OrderStatus from(String orderStatus) {
        // 입력받은 주문 상태가 공백일 때
        if (orderStatus == null || orderStatus.isBlank()) {
            throw new ServiceException(ErrorCode.INVALID_STATUS);
            // 잘못 입력했다고 생각하고 잘못된 입력값 return
        }
        // 상태 찾기
        for (OrderStatus r : OrderStatus.values()) {
            if (OrderStatus.valueOf(orderStatus) == r) {
                return r;
            }
        }
        // 입력값이 이상 할 때
        throw new ServiceException(ErrorCode.INVALID_STATUS);
    }
}
