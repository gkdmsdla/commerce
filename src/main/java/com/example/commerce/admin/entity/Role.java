package com.example.commerce.admin.entity;

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
}
