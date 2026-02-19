package com.example.commerce.admin.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    SUPER_ADMIN("A01", "총관리자"),
    OP_ADMIN("A02","운영 관리자"),
    CS_ADMIN("A03","고객 지원 관리자");

    private final String id;
    private final String name;
}
