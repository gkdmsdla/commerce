package com.example.commerce.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64; // Base64 디코딩을 위해 추가
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationTime;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = secretKey.getBytes();
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    // 1. Access Token 생성 (상세 정보 포함)
    public String createToken(Long id, String email, String role) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email)
                .claim("id", id)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationTime)) // 1시간 설정 주입
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. Refresh Token 생성 (최소 정보만 포함)
    public String createRefreshToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshExpirationTime)) // 14일 설정 주입
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 3. 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("유효하지 않은 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // 4. 토큰에서 정보 추출
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}