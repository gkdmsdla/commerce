package com.example.commerce.global.security;

import com.example.commerce.admin.entity.Admin;
import com.example.commerce.admin.repository.AdminRepository;
import com.example.commerce.customer.entity.Customer;
import com.example.commerce.customer.entity.CustomerStatus;
import com.example.commerce.customer.repository.CustomerRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;
    // 고객 DB 조회 위해 CustomerRepository 주입

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Header에서 토큰 추출
        String bearerToken = request.getHeader("Authorization");
        String token = null;
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7); // "Bearer " 이후의 순수 토큰만 추출
        }

        // 2. 토큰 유효성 검증 및 SecurityContext 에 인증 정보 저장
        if (token != null && jwtUtil.validateToken(token)) {
            Claims claims = jwtUtil.getUserInfoFromToken(token);
            String email = claims.getSubject();

            // 토큰에서 사용자의 '역할(Role)'을 꺼냄.
            // JwtUtil의 createToken 메서드에서 claim("role", role)로 넣은 값
            String role = claims.get("role", String.class);

            // 3-A. 접근한 사용자가 고객(CUSTOMER)인 경우
            if ("CUSTOMER".equals(role)) {
                Customer customer = customerRepository.findByEmail(email).orElse(null);

                // 고객이 DB에 존재하고, 활성 상태인지 확인
                if (customer != null && customer.getStatus() == CustomerStatus.ACTIVE) {
                    // (주의: CustomerUserDetails 클래스는 별도로 만들어 주셔야 합니다)
                    CustomerUserDetails userDetails = new CustomerUserDetails(customer);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            // 3-B. 접근한 사용자가 관리자(SUPER_ADMIN, OP_ADMIN, CS_ADMIN)인 경우
            else {
                Admin admin = adminRepository.findByEmail(email).orElse(null);

                // 기존 로직과 동일
                if (admin != null && admin.getStatus().isLoginable()) {
                    AdminUserDetails userDetails = new AdminUserDetails(admin);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        // 다음 필터로 요청을 넘김
        filterChain.doFilter(request, response);
    }
}