package com.example.commerce.admin.dto;

import com.example.commerce.admin.entity.AdminStatus;
import com.example.commerce.admin.entity.Role;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LoginResponse {
    private final Long id;
    private final String name;
    private final String email;
    private final String phone;
    private final Role role;
    private final AdminStatus status;
    private final LocalDateTime createdAt;

    public LoginResponse(Long id, String name, String email, String phone, Role role, AdminStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }
}
