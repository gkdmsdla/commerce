package com.example.commerce.admin.service;

import com.example.commerce.admin.dto.*;
import com.example.commerce.admin.entity.Admin;
import com.example.commerce.admin.entity.AdminStatus;
import com.example.commerce.admin.repository.AdminRepository;
import com.example.commerce.global.config.PasswordEncoder;
import com.example.commerce.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import com.example.commerce.global.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (adminRepository.existsByEmail(request.getEmail())){
            throw new ServiceException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Admin admin = new Admin(
                request.getName(),
                request.getEmail(),
                encodedPassword,
                request.getPhone(),
                request.getRole(),
                AdminStatus.PENDING
        );

        Admin savedAdmin = adminRepository.save(admin);

        return new SignupResponse(
                savedAdmin.getId(),
                savedAdmin.getName(),
                savedAdmin.getEmail(),
                savedAdmin.getPhone(),
                savedAdmin.getRole(),
                savedAdmin.getStatus()
        );
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.getEmail()).orElseThrow(
                ()-> new ServiceException(ErrorCode.ADMIN_NOT_FOUND)
        );

        if(!passwordEncoder.matches(request.getPassword(), admin.getPassword())){
            throw new ServiceException(ErrorCode.WRONG_PW);
        }

        if(!admin.getStatus().isLoginable()){
            throw new ServiceException(ErrorCode.FORBIDDEN_ADMIN);
        }

        return new LoginResponse(
                admin.getId(),
                admin.getName(),
                admin.getEmail(),
                admin.getPhone(),
                admin.getRole(),
                admin.getStatus(),
                admin.getCreatedAt()
        );
    }
}
