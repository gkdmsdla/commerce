package com.example.commerce.admin.dto;

import com.example.commerce.admin.entity.Admin;
import com.example.commerce.admin.entity.AdminStatus;
import com.example.commerce.admin.entity.Role;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AdminDetailResponse {
    private final Long id;
    private final String name;
    private final String email;
    private final String phone;
    private final Role role;
    private final AdminStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime approvedAt;

    private AdminDetailResponse(Long id, String name, String email, String phone, Role role, AdminStatus status, LocalDateTime createdAt, LocalDateTime approvedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.approvedAt = approvedAt;
    }

    // 시니어의 팁: 엔티티를 통째로 받아서 DTO로 변환하는 '정적 팩토리 메서드' 패턴입니다.
    // 이렇게 하면 Service 계층의 코드가 아주 깔끔해집니다.
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