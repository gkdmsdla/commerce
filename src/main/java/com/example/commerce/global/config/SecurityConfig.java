package com.example.commerce.global.config;

import com.example.commerce.global.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 비활성화 (JWT를 사용하므로 불필요함)
                .csrf(csrf -> csrf.disable())

                // 2. 세션 관리 정책: STATELESS (서버가 세션을 생성하지도, 사용하지도 않음)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. 엔드포인트 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // Admin과 Customer의 회원가입/로그인 경로는 인증 없이 접근 허용
                        .requestMatchers(
                                "/admins/signup",
                                "/admins/login",
                                "/customers/signup",
                                "/customers/login",
                                "/products",
                                "/products/**"

                        ).permitAll()
                        // 그 외의 모든 요청은 인증(토큰) 필요
                        .anyRequest().authenticated()
                )

                // 4. JWT 필터 등록: 기본 인증 필터(UsernamePasswordAuthenticationFilter)보다 먼저 실행되도록 조치
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}