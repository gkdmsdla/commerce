package com.example.commerce.admin.entity;

import com.example.commerce.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "admins")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    private String phone;

    @NotNull(message = "정확한 관리자 직책을 입력해주세요.")
    @Enumerated(EnumType.STRING) // Enum 값을 숫자가 아닌 문자열 그 자체로 받아 DB 저장
    private Role role;

    @Enumerated(EnumType.STRING)
    private AdminStatus status;

    // 승인, 거부 처리를 위해 필드 추가하였음.
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private String rejectReason;

    public Admin(String name, String email, String password, String phone, Role role, AdminStatus status) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.status = status;
    }

    public void update(String name, String email, String phone){
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // 상태 변경을 엔티티 내부 메서드를 통해서 수행
    public void approve() {
        this.status = AdminStatus.ACTIVE;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject(String reason){
        this.status = AdminStatus.REJECTED;
        this.rejectedAt = LocalDateTime.now();
        this.rejectReason = reason;
    }

    //  관리자 역할 및 상태 변경 로직

    public void updateRole(Role newRole) {
        this.role = newRole;
    }

    public void updateStatus(AdminStatus newStatus) {
        this.status = newStatus;
    }
}
