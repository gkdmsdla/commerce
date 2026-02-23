package com.example.commerce.customer.repository;

import com.example.commerce.customer.entity.Customer;
import com.example.commerce.customer.entity.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // 이메일 중복체크
    boolean existsByEmail(String email);
    // 등록된 이메일 확인
    Optional<Customer> findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE " +
            "(:keyword IS NULL OR c.name LIKE %:keyword% OR c.email LIKE %:keyword%) AND " +
            "(:status IS NULL OR c.status = :status)")
    Page<Customer> searchCustomers(
            @Param("keyword")String keyword,
            @Param("status") CustomerStatus status,
            PageRequest pageable);

}
