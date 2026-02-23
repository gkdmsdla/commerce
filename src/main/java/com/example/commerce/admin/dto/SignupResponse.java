package com.example.commerce.admin.dto;

import com.example.commerce.admin.entity.AdminStatus;
import com.example.commerce.admin.entity.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public record SignupResponse (
        Long adminId,
        String adminName,
        String adminEmail,
        String adminPhone,
        String adminRole,
        String adminStatus
){}

//@RequiredArgsConstructor
//@Getter
//public class SignupResponse {
//    private final Long adminId;
//    private final String adminName;
//    private final String adminEmail;
//    private final String adminPhone;
////    private final Role adminRole;
////    private final AdminStatus adminStatus;
//    private final String adminRole;
//    private final String adminStatus;
//}
