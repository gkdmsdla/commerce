package com.example.commerce.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateRoleRequest {
    @NotBlank(message = "변경할 직책은 필수입니다.")
    private String role;
}