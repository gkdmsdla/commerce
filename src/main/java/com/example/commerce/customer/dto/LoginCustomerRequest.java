package com.example.commerce.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginCustomerRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식과 일치해야합니다.")
    private String customerEmail;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String customerPassword;
}
