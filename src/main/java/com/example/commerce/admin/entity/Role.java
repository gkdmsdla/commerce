package com.example.commerce.admin.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    SUPER_ADMIN("A01", "총관리자",3),
    OP_ADMIN("A02","운영 관리자",2),
    CS_ADMIN("A03","고객 지원 관리자",1);

    private final String id;
    private final String name;
    private final int level;
}
