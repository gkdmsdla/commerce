package com.example.commerce.admin.repository;

import com.example.commerce.admin.entity.Admin;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsByEmail(String email);

    Optional<Admin> findByEmail(@NotBlank String email);
}
