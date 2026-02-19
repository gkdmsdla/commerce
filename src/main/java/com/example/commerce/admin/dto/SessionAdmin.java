package com.example.commerce.admin.dto;

import com.example.commerce.admin.entity.Role;
import lombok.Getter;

@Getter
public class SessionAdmin {
    private final Long id;
    private final String email;
    private final Role role;

    public SessionAdmin(LoginResponse response) {
        this.id = response.getId();
        this.email = response.getEmail();
        this.role = response.getRole();
    }
}
