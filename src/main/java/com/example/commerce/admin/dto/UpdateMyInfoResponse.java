package com.example.commerce.admin.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


public record UpdateMyInfoResponse(
        String myName,
        String myEmail,
        String myPhone
){}

//@Getter
//@RequiredArgsConstructor
//public class UpdateMyInfoResponse {
//    private final String myName;
//    private final String myEmail;
//    private final String myPhone;
//}
