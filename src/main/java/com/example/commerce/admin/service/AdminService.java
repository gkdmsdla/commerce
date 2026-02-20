package com.example.commerce.admin.service;

import com.example.commerce.admin.dto.*;
import com.example.commerce.admin.entity.Admin;
import com.example.commerce.admin.entity.AdminStatus;
import com.example.commerce.admin.entity.Role;
import com.example.commerce.admin.repository.AdminRepository;
import com.example.commerce.global.config.PasswordEncoder;
import com.example.commerce.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import com.example.commerce.global.exception.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

//
//        if(!admin.getStatus().isLoginable()){
//            throw new ServiceException(ErrorCode.FORBIDDEN_ADMIN);
//        }

        if(!admin.getStatus().isLoginable()){
            switch (admin.getStatus()) {
                case PENDING -> throw new ServiceException(ErrorCode.ADMIN_PENDING);   // "계정 승인대기 중"
                case REJECTED -> throw new ServiceException(ErrorCode.ADMIN_REJECTED); // "계정 신청 거부됨"
                case STOPPED -> throw new ServiceException(ErrorCode.ADMIN_STOPPED);   // "계정 정지됨"
                case INACTIVE -> throw new ServiceException(ErrorCode.ADMIN_INACTIVE); // "계정 비활성화됨"
                default -> throw new ServiceException(ErrorCode.FORBIDDEN_ADMIN);
            }
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

    // 관리자 승인 (JPA 변경 감지 활용)
    @Transactional
    public void approveAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ADMIN_NOT_FOUND));

        if (admin.getStatus() != AdminStatus.PENDING) {
            throw new ServiceException(ErrorCode.INVALID_STATUS); // 대기 상태가 아니면 승인 불가
        }

        // 트랜잭션 내에서 엔티티 필드만 변경하면 JPA가 알아서 UPDATE 쿼리 요청함 (Dirty Checking)
        admin.approve();
    }

        //관리자 리스트 페이징 조회
    @Transactional(readOnly = true)
    public Page<AdminDetailResponse> getAdminList(String keyword, Role role, AdminStatus status, Pageable pageable) {
        // 1. Repository의 동적 쿼리를 호출하여 엔티티 페이징 객체를 가져옴
        Page<Admin> admins = adminRepository.searchAdmins(keyword, role, status, pageable);

        // 2. Page<Admin>을 Page<AdminDetailResponse>로 변환 (DTO 변환)
        return admins.map(AdminDetailResponse::from);
    }

    @Transactional(readOnly = true)
    public AdminDetailResponse getAdminDetail(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ADMIN_NOT_FOUND));

        return AdminDetailResponse.from(admin);
    }
    // 관리자 정보/내 프로필 수정
    @Transactional
    public UpdateAdminResponse updateAdminInfo(Long adminId, UpdateAdminRequest request) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ADMIN_NOT_FOUND));

        // 보안 체크: 만약 이메일을 변경하려고 하는데, 그 이메일이 이미 다른 사람의 것이라면 막음
        if (!admin.getEmail().equals(request.getEmail()) && adminRepository.existsByEmail(request.getEmail())) {
            throw new ServiceException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 엔티티 내부의 수정 메서드 호출 (Dirty Checking)
        admin.update(request.getName(), request.getEmail(), request.getPhone());

        return new UpdateAdminResponse(
                admin.getName(),
                admin.getEmail(),
                admin.getPhone()
        );
    }

    @Transactional
    public void rejectAdmin(Long adminId, String reason) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ADMIN_NOT_FOUND));

        if (admin.getStatus() != AdminStatus.PENDING) {
            throw new ServiceException(ErrorCode.INVALID_STATUS); // "승인 대기 상태가 아닙니다" 에러 추가 필요
        }

        // 엔티티 내부의 거부 메서드 호출 (상태 변경 및 거부 사유, 거부일시 저장)
        admin.reject(reason);
    }

    public GetAdminResponse getOne(long adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(
                ()-> new ServiceException(ErrorCode.ADMIN_NOT_FOUND)
        );

        return new GetAdminResponse(
                admin.getName(),
                admin.getEmail(),
                admin.getPhone(),
                admin.getRole().getName(),
                admin.getStatus().getTitle(),
                admin.getCreatedAt(),
                admin.getApprovedAt()
        );
    }

    public GetMyInfoResponse getMyInfo(Long sessionAdminId) {
        Admin admin = adminRepository.findById(sessionAdminId).orElseThrow(
                ()->new ServiceException(ErrorCode.ADMIN_NOT_FOUND)
        );

        return new GetMyInfoResponse(
                admin.getName(),
                admin.getEmail(),
                admin.getPhone()
        );
    }

    public UpdateMyInfoResponse updateMyInfo(Long sessionAdminId, UpdateMyInfoRequest request) {
        Admin admin = adminRepository.findById(sessionAdminId).orElseThrow(
                ()->new ServiceException(ErrorCode.ADMIN_NOT_FOUND)
        );

        // 중복 email 이라면 throw 중복 EMAIL 오류
        if(adminRepository.existsByEmail(request.getMyEmail())){
            throw new ServiceException(ErrorCode.DUPLICATE_EMAIL);
        }

        //Update, 위에서 repository 에 갔다왔으므로 자동 update 가 가능
        admin.update(request.getMyName(), request.getMyEmail(), request.getMyPhone());

        return new UpdateMyInfoResponse(
                admin.getName(),
                admin.getEmail(),
                admin.getPhone()
        );
    }
}
