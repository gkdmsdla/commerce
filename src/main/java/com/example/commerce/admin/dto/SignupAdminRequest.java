package com.example.commerce.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignupAdminRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식과 일치해야합니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min=8, max = 20)
    private String password;

    @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", message = "전화번호 형식은 010-XXXX-XXXX 입니다.")
    private String phone;

    @NotBlank(message = "직책은 필수입니다.")
    private String role; // 일단 String 으로 role 받고 Service 에서 확인
}
