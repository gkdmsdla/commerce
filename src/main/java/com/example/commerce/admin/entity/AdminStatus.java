package com.example.commerce.admin.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminStatus {
    // [명칭, 로그인 가능 여부]
    PENDING("승인대기", false),
    ACTIVE("활성", true),
    INACTIVE("비활성", false),
    STOPPED("정지", false),
    REJECTED("거부", false);

    private final String title;
    private final boolean loginable; // [로그인 처리] 반영
}