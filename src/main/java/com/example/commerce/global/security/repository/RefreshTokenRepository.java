package com.example.commerce.global.security.repository;

import com.example.commerce.global.security.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByEmail(String email);
    Optional<RefreshToken> findByToken(String token);

    // 로그아웃 시 토큰 삭제를 위한 메서드
    void deleteByEmail(String email);
}