package com.example.commerce.admin.dto;

import com.example.commerce.admin.entity.Admin;
import com.example.commerce.admin.entity.AdminStatus;
import com.example.commerce.admin.entity.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminDetailResponse {
    private final Long id;
    private final String name;
    private final String email;
    private final String phone;
    private final Role role;
    private final AdminStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime approvedAt;

    // 객체 생성용 정적 팩토리 메서드 입니다.
    public static AdminDetailResponse from(Admin admin) {
        return new AdminDetailResponse(
                admin.getId(),
                admin.getName(),
                admin.getEmail(),
                admin.getPhone(),
                admin.getRole(),
                admin.getStatus(),
                admin.getCreatedAt(),
                admin.getApprovedAt()
        );
    }
}