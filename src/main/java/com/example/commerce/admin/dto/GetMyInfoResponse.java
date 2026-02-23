package com.example.commerce.admin.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


public record GetMyInfoResponse (
    String myName,
    String myEmail,
    String myPhone
    ){}

//@Getter
//@RequiredArgsConstructor
//public class GetMyInfoResponse {
//    private final String myName;
//    private final String myEmail;
//    private final String myPhone;
//}
