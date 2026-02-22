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
    //response 의 경우 조금 더 자세한 정보를 front 에 주는게 좋다고 하셔서 admin 이라고 붙였습니다
    private final Long adminId;
    private final String adminName;
    private final String adminEmail;
    private final String adminPhone;
    private final Role adminRole;
    private final AdminStatus adminStatus;
    private final LocalDateTime signupAt; // createdAt 이 들어갑니다! response 라서 signupAt 이라고 작성했어요
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