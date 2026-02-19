package com.example.commerce.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고객 고유 ID

    @Column(nullable = false)
    private String name; // 고객 이름

    @Column(nullable = false, unique = true)
    private String email; // 고객 이메일

    @Column(nullable = false)
    private String phone; // 고객 전화번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus status; // 고객 상태

    @Column(nullable = false)
    private LocalDateTime createAt; // 가입일

    @Column(nullable = false)
    private LocalDateTime modifiedAt; // 수정일

    // 고객 생성 시 시간 자동 입력
    @PrePersist
    public void timeCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createAt = now;
        this.modifiedAt = now;

        if (this.status == null) {
            this.status = CustomerStatus.ACTIVE;
        }
    }

    // 수정할 때 자동 갱신
    @PreUpdate
    public void timeUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }

    // 고객 정보 변경
    public void update(String name, String email, String phone) {
        if (name != null) this.name = name;
        if (email != null) this.email = email;
        if (phone != null) this.phone = phone;
    }

    // 상태 변경
    public void statusUpdate(CustomerStatus status) {
        this.status = status;
    }
}
