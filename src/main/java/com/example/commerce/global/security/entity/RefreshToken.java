package com.example.commerce.global.security.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email; // 사용자 식별자 (Admin, Customer 공통 활용)

    @Column(nullable = false, length = 500)
    private String token; // 리프레시 토큰 값

    public RefreshToken(String email, String token) {
        this.email = email;
        this.token = token;
    }

    // 기존 로그인 유저가 다시 로그인하면 토큰 값만 교체하기 위한 메서드
    public void updateToken(String token) {
        this.token = token;
    }
}