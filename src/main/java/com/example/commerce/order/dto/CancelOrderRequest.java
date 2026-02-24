package com.example.commerce.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CancelOrderRequest {

    @NotBlank(message = "취소 사유는 필수입니다.")
    private String cancelReason;

}