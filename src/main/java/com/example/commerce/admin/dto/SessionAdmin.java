package com.example.commerce.admin.dto;

import com.example.commerce.admin.entity.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class SessionAdmin {
    private final Long id;
    private final String email;
    private final Role role;

    public SessionAdmin(LoginResponse response) {
        this.id = response.getAdminId();
        this.email = response.getAdminEmail();
        this.role = response.getAdminRole();
    }
}
