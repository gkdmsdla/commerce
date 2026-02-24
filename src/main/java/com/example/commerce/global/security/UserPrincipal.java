package com.example.commerce.global.security;

public interface UserPrincipal {
    Long getId(); // 사용자 고유 식별자
    String getEmail(); // 로그인 이메일 (필터나 서비스에서 조회 시 활용)
    String getRole(); // 권한 정보 (ADMIN, CUSTOMER 등)
}
