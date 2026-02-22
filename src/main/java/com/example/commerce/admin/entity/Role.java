package com.example.commerce.admin.entity;

import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ROLE_SUPER_ADMIN("A01", "총관리자",3),
    ROLE_OP_ADMIN("A02","운영 관리자",2),
    ROLE_CS_ADMIN("A03","고객 지원 관리자",1);
    // Spring Security 사용을 위해 접두사(ROLE) 추가

    private final String id;
    private final String name;
    private final int level;

    // 역할 String 을 Enum 타입으로 변환
    public static Role from(String role) {
        // 입력받은 직책이 공백
        if (role == null || role.isBlank()) {
            throw new ServiceException(ErrorCode.INVALID_ROLE);
            // 잘못 입력했다고 생각하고 잘못된 입력값 return
        }

        // 직책 찾기
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(role)) {
                return r;
            }
        }

        throw new ServiceException(ErrorCode.INVALID_ROLE);

    }
}
