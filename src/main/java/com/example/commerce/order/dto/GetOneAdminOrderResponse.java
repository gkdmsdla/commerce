package com.example.commerce.order.dto;

import com.example.commerce.admin.entity.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class GetOneAdminOrderResponse {
    // 단 건 주문 조회 응답

    // 주문
    private final long orderNo;
    private final int orderQuantity;
    private final String orderStatus;

    // 고객
    private final String customerName;
    private final String customerEmail;

    //상품
    private final String productName;
    private final int productPrice;

    private final LocalDateTime createdAt;

    //관리자
    private final String adminName; //주문 등록 관리자명
    private final String adminEmail;
    private final Role adminRole;
}


