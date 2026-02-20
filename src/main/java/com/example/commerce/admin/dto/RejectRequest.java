package com.example.commerce.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RejectRequest {
    @NotBlank(message = "거부 사유는 필수 입력 항목입니다.")
    private String reason;
}