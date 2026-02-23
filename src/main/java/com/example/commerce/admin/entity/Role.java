package com.example.commerce.admin.entity;

import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    SUPER_ADMIN("A01", "총관리자",3),
    OP_ADMIN("A02","운영 관리자",2),
    CS_ADMIN("A03","고객 지원 관리자",1);
    // Spring Security 사용을 위해 접두사(ROLE) 추가

    private final String id;
    private final String roleName;
    private final int level;

    // 역할 String 을 Enum 타입으로 변환
    public static Role from(String role) {
        //System.out.println("role: "+ role);
        // 입력받은 직책이 공백
        if (role == null || role.isBlank()) {
            //System.out.println("role exception here1");
            throw new ServiceException(ErrorCode.INVALID_ROLE);
            // 잘못 입력했다고 생각하고 잘못된 입력값 return
        }

        // 직책 찾기
        for (Role r : Role.values()) {
            //System.out.println("role exception here2: "+r);
            if (Role.valueOf(role) == r) {
                return r;
            }
        }

        throw new ServiceException(ErrorCode.INVALID_ROLE);
    }
}
