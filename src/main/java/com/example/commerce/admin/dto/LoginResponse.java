package com.example.commerce.admin.dto;

import com.example.commerce.admin.entity.AdminStatus;
import com.example.commerce.admin.entity.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class LoginResponse {
    private final Long adminId;
    private final String adminName;
    private final String adminEmail;
    private final String adminPhone;
    private final Role adminRole;
    private final AdminStatus adminStatus;
    private final LocalDateTime adminCreatedAt;
}
