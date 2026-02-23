package com.example.commerce.customer.repository;

import com.example.commerce.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // 이메일 중복체크
    boolean existsByEmail(String email);
    // 등록된 이메일 확인
    Optional<Customer> findByEmail(String email);
}
