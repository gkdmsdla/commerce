package com.example.commerce.admin.dto;

import com.example.commerce.admin.entity.AdminStatus;
import com.example.commerce.admin.entity.Role;
import lombok.Getter;

@Getter
public class SignupResponse {
    private final Long adminId;
    private final String adminName;
    private final String adminEmail;
    private final String adminPhone;
    private final Role adminRole;
    private final AdminStatus adminStatus;

    public SignupResponse(Long adminId, String adminName, String adminEmail, String adminPhone, Role adminRole, AdminStatus adminStatus) {
        this.adminId = adminId;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.adminPhone = adminPhone;
        this.adminRole = adminRole;
        this.adminStatus = adminStatus;
    }
}
