package com.example.commerce.admin.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


public record UpdateAdminResponse(
        String adminName,
        String adminEmail,
        String adminPhone
){}

//@Getter
//@RequiredArgsConstructor
//public class UpdateAdminResponse {
//    private final String adminName;
//    private final String adminEmail;
//    private final String adminPhone;
//}
