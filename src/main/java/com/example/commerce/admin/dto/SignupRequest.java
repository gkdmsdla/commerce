package com.example.commerce.admin.dto;

import com.example.commerce.admin.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignupRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email(message = "이메일 형식과 일치해야합니다.")
    private String email;

    @NotBlank
    @Size(min=8, max = 20)
    private String password;

    @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$")
    private String phone;

    //Enum 에 등록된 역할만 입력받았는지 확인
    private String role; // 일단 String 으로 role 받고 Service 에서 확인
}
