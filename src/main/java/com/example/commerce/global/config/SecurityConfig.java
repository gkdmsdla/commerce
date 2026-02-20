package com.example.commerce.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 회원가입, 로그인은 누구나 접근 가능
                        .requestMatchers("/api/admins/signup", "/api/admins/login").permitAll()
                        // 그 외 모든 요청은 인증(로그인) 필요
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
