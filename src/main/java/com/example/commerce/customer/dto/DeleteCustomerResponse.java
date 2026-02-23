package com.example.commerce.customer.dto;


import lombok.RequiredArgsConstructor;
import lombok.Getter;

@Getter
@RequiredArgsConstructor
public class DeleteCustomerResponse {

    // 고객 삭제 시 "삭제되었습니다" 메세지 출력

    private final String customerMessage;
}
