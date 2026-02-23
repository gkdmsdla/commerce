package com.example.commerce.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateStatusRequest {
    @NotBlank(message = "변경할 상태는 필수입니다.")
    private String status;
}
