package com.example.commerce.admin.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class GetAdminResponse {
    private final String adminName;
    private final String adminEmail;
    private final String adminPhone;
    private final String adminRole;
    private final String adminStatus;
    // createdAt 이 들어갑니다! response 라서 signupAt 이라고 작성했어요
    private final LocalDateTime signupAt;
    private final LocalDateTime approvedAt;
}
