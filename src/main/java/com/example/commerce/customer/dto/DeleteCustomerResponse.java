package com.example.commerce.customer.dto;


public record DeleteCustomerResponse(
        String customerMessage// 고객 삭제 시 "삭제되었습니다" 메세지 출력
) {}
