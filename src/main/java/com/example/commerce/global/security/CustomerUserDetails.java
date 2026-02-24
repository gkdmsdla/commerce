package com.example.commerce.global.security;

import com.example.commerce.customer.entity.Customer;
import com.example.commerce.customer.entity.CustomerStatus; // 상태 검사를 위해 추가
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomerUserDetails implements UserDetails,UserPrincipal {
    // session 대신 Spring Security가 사용하는 로그인 된 사용자 정보 객체

    private final Customer customer;

    @Override
    public Collection<SimpleGrantedAuthority> getAuthorities() {
        // 고객 엔티티에는 세부 Role이 없으므로 일괄적으로 "ROLE_CUSTOMER" 권한 부여
        return List.of(
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );
    }

    @Override
    public String getPassword() {
        return customer.getPassword();
    }

    @Override
    public String getUsername() {
        return customer.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 고객의 상태가 활성(ACTIVE)일 때만 계정 사용 가능 처리
        return customer.getStatus() == CustomerStatus.ACTIVE;
    }

    @Override
    public Long getId() {
        return customer.getId();
    }

    @Override
    public String getEmail() {
        return customer.getEmail();
    }

    @Override
    public String getRole() {
        return "CUSTOMER";
    }
}