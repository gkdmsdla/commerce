package com.example.commerce.admin.repository;

import com.example.commerce.admin.entity.Admin;
import com.example.commerce.admin.entity.AdminStatus;
import com.example.commerce.admin.entity.Role;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsByEmail(String email);
    Optional<Admin> findByEmail(String email);

    // 키워드, 역할, 상태를 모두 조합하여 검색하는 동적 쿼리 (파라미터가 null이면 해당 조건 무시)
    /*
    SELECT a FROM Admin a WHERE : Admin 에서 조건에 맞는 것을 고르기
    keyword, role, status 가 없으면 조건에서 빠짐
    있으면 LIKE 를 통해서 키워드가 포함된 것 / role 이 같은 것 / status 가 같은 것을 고름
    */
    @Query("SELECT a FROM Admin a WHERE " +
            "(:keyword IS NULL OR a.name LIKE %:keyword% OR a.email LIKE %:keyword%) AND " +
            "(:role IS NULL OR a.role = :role) AND " +
            "(:status IS NULL OR a.status = :status)")
    Page<Admin> searchAdmins(
            @Param("keyword") String keyword,
            @Param("role") Role role,
            @Param("status") AdminStatus status,
            Pageable pageable
    );
}
