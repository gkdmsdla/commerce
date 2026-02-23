package com.example.commerce.order.dto;

import com.example.commerce.admin.entity.Role;

import java.time.LocalDateTime;
import java.util.UUID;


public record GetOneAdminOrderResponse(
        // 단 건 주문 조회 응답

        // 주문
        UUID orderNo,
        int orderQuantity,
        String orderStatus,

        // 고객
        String customerName,
        String customerEmail,

        //상품
        String productName,
        int productPrice,

        LocalDateTime createdAt,

        //관리자
        String adminName,
        String adminEmail,
        Role adminRole
) { }


