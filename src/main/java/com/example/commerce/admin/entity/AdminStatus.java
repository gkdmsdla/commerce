package com.example.commerce.admin.entity;

import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
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

    private final String statusName;
    private final boolean loginable; // [로그인 처리] 반영

    public static AdminStatus from(String status) {
        // 입력받은 직책이 공백
        if (status == null || status.isBlank()) {
            throw new ServiceException(ErrorCode.INVALID_STATUS);
            // 잘못 입력했다고 생각하고 잘못된 입력값 return
        }

        // 직책 찾기
        for (AdminStatus a : AdminStatus.values()) {
            if (AdminStatus.valueOf(status) == a ) {
                return a;
            }
        }

        throw new ServiceException(ErrorCode.INVALID_STATUS);
    }
}