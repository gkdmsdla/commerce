package com.example.commerce.admin.entity;

import com.example.commerce.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Role role;

    private AdminStatus status;

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
}
