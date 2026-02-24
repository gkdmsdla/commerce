package com.example.commerce.global.config;

import com.example.commerce.admin.entity.Admin;
import com.example.commerce.admin.entity.AdminStatus;
import com.example.commerce.admin.entity.Role;
import com.example.commerce.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class InitialAdminConfig {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // 1. 관리자가 한 명도 없는지 확인
            if (adminRepository.count() == 0) {
                // 2. 최초의 슈퍼 관리자 정보 설정
                Admin superAdmin = new Admin(
                        "테스트용 슈퍼관리자",
                        "admin@test.com",
                        passwordEncoder.encode("password123!"), // 초기 비밀번호
                        "010-1234-5678",
                        Role.SUPER_ADMIN,
                        AdminStatus.ACTIVE // 서버 시작 시 관리자가 한 명도 없다면 자동으로 테스트용 슈퍼 관리자 활성화
                );

                adminRepository.save(superAdmin);
                System.out.println(">>> [System] 최초 슈퍼 관리자(테스트용 슈퍼관리자) 계정이 생성되었습니다.");
            }
        };
    }
}