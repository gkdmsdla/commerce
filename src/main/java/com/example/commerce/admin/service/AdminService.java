package com.example.commerce.admin.service;

import com.example.commerce.admin.dto.*;
import com.example.commerce.admin.entity.Admin;
import com.example.commerce.admin.entity.AdminStatus;
import com.example.commerce.admin.entity.Role;
import com.example.commerce.admin.repository.AdminRepository;
import com.example.commerce.global.config.PasswordEncoder;
import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
import com.example.commerce.global.security.JwtUtil;
import com.example.commerce.global.security.entity.RefreshToken;
import com.example.commerce.global.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final JwtUtil jwtUtil; // jwt 토큰 사용을 위해 작성
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository; // 리프레시 토큰 사용 위해 활용

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // request 로 들어온 email 이 이미 존재하는지 확인,
        // 존재한다면 DUPLICATE_EMAIL 409 conflict 에러 발생시킴
        if (adminRepository.existsByEmail(request.getEmail())){
            throw new ServiceException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 비밀번호는 암호화 후 저장됨
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        //request 에서 string 으로 들어온 role 값을 enum 타입으로 변환
        Role role = Role.from(request.getRole());

        Admin admin = new Admin(
                request.getName(),
                request.getEmail(),
                encodedPassword,
                request.getPhone(),
                role,
                AdminStatus.PENDING // 새로 생성되는 관리자의 상태는 모두 승인 대기중
        );

        // DB에 저장
        Admin savedAdmin = adminRepository.save(admin);

        return new SignupResponse(
                savedAdmin.getId(),
                savedAdmin.getName(),
                savedAdmin.getEmail(),
                savedAdmin.getPhone(),
                savedAdmin.getRole().getRoleName(),
                savedAdmin.getStatus().getStatusName()
        );
    }



    @Transactional
    public LoginResponse login(LoginRequest request) { // HttpServletRequest 파라미터 삭제
        Admin admin = adminRepository.findByEmail(request.getEmail()).orElseThrow(
                ()-> new ServiceException(ErrorCode.ADMIN_NOT_FOUND)
        );

        if(!passwordEncoder.matches(request.getPassword(), admin.getPassword())){
            throw new ServiceException(ErrorCode.WRONG_PW);
        }

        isActiveAdmin(admin);

        // 1. Access Token 및 Refresh Token 동시 생성
        String accessToken = jwtUtil.createToken(admin.getId(), admin.getEmail(), admin.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(admin.getEmail());

        // 2. Refresh Token DB 저장 (이미 존재하면 Update, 없으면 Insert)
        RefreshToken tokenEntity = refreshTokenRepository.findByEmail(admin.getEmail())
                .orElse(new RefreshToken(admin.getEmail(), refreshToken));

        tokenEntity.updateToken(refreshToken);
        refreshTokenRepository.save(tokenEntity);

        // 3. LoginResponse 반환 (accessToken을 프론트엔드로 전달)
        return new LoginResponse(
                accessToken, // 기존 token 자리에 accessToken 넣기
                admin.getId(),
                admin.getName(),
                admin.getEmail(),
                admin.getPhone(),
                admin.getRole().getRoleName(),
                admin.getStatus().getStatusName(),
                admin.getCreatedAt()
        );
    }

    // 관리자 승인 (JPA 변경 감지 활용)
    @Transactional
    public void approveAdmin(Long adminId, Long sessionAdminId) {
        //현재 승인 또는 거부하려는 슈퍼관리자가 존재하며, 활성상태인지 확인
        isActiveAdmin(getAdminById(sessionAdminId));

        // 승인 또는 거부하려는 대상 관리자가 존재하는지 확인
        Admin admin = getAdminById(adminId);
//        Admin admin = adminRepository.findById(adminId)
//                .orElseThrow(() -> new ServiceException(ErrorCode.ADMIN_NOT_FOUND));

        // 승인 또는 거부하려는 대상 관리자가 승인 대기 상태인지 확인
        if (admin.getStatus() != AdminStatus.PENDING) {
            throw new ServiceException(ErrorCode.INVALID_STATUS); // 대기 상태가 아니면 승인 불가
        }

        // 트랜잭션 내에서 엔티티 필드만 변경하면 JPA가 알아서 UPDATE 쿼리 요청함 (Dirty Checking)
        admin.approve();
    }

        //관리자 리스트 페이징 조회
    @Transactional(readOnly = true)
    public Page<AdminDetailResponse> getAdminList(long sessionAdminId, String keyword, Role role, AdminStatus status, Pageable pageable) {
        //admin id 로 admin 을 찾고, 활성상태인지 확인
        isActiveAdmin(getAdminById(sessionAdminId));

        // 1. Repository의 동적 쿼리를 호출하여 엔티티 페이징 객체를 가져옴
        Page<Admin> admins = adminRepository.searchAdmins(keyword, role, status, pageable);

        // 2. Page<Admin>을 Page<AdminDetailResponse>로 변환 (DTO 변환)
        //return admins.map(AdminDetailResponse::from);
        return admins.map(admin -> new AdminDetailResponse(
                admin.getId(),
                admin.getName(),
                admin.getEmail(),
                admin.getPhone(),
                admin.getRole().getRoleName(),
                admin.getStatus().getStatusName(),
                admin.getCreatedAt(),
                admin.getApprovedAt()
        ));
    }

    // 개별 관리자의 상세정보 조회
    @Transactional(readOnly = true)
    public AdminDetailResponse getAdminDetail(Long adminId, Long sessionAdminId) {
        isActiveAdmin(getAdminById(sessionAdminId)); // 로그인 한 관리자가 활성상태인지 확인

        // 찾으려는 관리자가 존재하는지 확인
        Admin admin = getAdminById(adminId);

        //return AdminDetailResponse.from(admin);
        return new AdminDetailResponse(
                admin.getId(),
                admin.getName(),
                admin.getEmail(),
                admin.getPhone(),
                admin.getRole().getRoleName(),
                admin.getStatus().getStatusName(),
                admin.getCreatedAt(),
                admin.getApprovedAt()
        );
    }
    // 관리자 정보/내 프로필 수정
    @Transactional
    public UpdateAdminResponse updateAdminInfo(Long adminId, UpdateAdminRequest request, Long sessionAdminId) {
        isActiveAdmin(getAdminById(sessionAdminId));
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
    public RejectResponse rejectAdmin(Long adminId, RejectRequest request, Long sessionAdminId) {
        //현재 승인 또는 거부하려는 슈퍼관리자가 존재하며, 활성상태인지 확인
        isActiveAdmin(getAdminById(sessionAdminId));

        // 승인 또는 거부하려는 대상 관리자가 존재하는지 확인
        Admin admin = getAdminById(adminId);

        // 승인 또는 거부하려는 대상 관리자가 승인 대기 상태인지 확인
        if (admin.getStatus() != AdminStatus.PENDING) {
            throw new ServiceException(ErrorCode.INVALID_STATUS); // 대기 상태가 아니면 승인 불가
        }

        // 엔티티 내부의 거부 메서드 호출 (상태 변경 및 거부 사유, 거부일시 저장)
        admin.reject(request.getReason());

        return new RejectResponse(
                admin.getId(),
                admin.getRole().getRoleName(),
                admin.getRejectReason(),
                admin.getRejectedAt()
        );
    }

    public Admin getAdminById(long adminId){
        return adminRepository.findById(adminId).orElseThrow(
                ()->new ServiceException(ErrorCode.ADMIN_NOT_FOUND)
        );
    }

    // 관리자가 활성상태가 맞는지 확인하는 로직
    public void isActiveAdmin(Admin admin){
        //isLoginable 은 활성(Active) 상태에서만 true 니까 활성상태가 아니라면 throw
        if(!admin.getStatus().isLoginable()){
            switch (admin.getStatus()) {
                case PENDING -> throw new ServiceException(ErrorCode.ADMIN_PENDING);   // "계정 승인대기 중"
                case REJECTED -> throw new ServiceException(ErrorCode.ADMIN_REJECTED); // "계정 신청 거부됨"
                case STOPPED -> throw new ServiceException(ErrorCode.ADMIN_STOPPED);   // "계정 정지됨"
                case INACTIVE -> throw new ServiceException(ErrorCode.ADMIN_INACTIVE); // "계정 비활성화됨"
                default -> throw new ServiceException(ErrorCode.FORBIDDEN_ADMIN);
            }
        }
    }

    @Transactional
    public void updateAdminRole(Long targetId, String roleString, Long sessionAdminId) {
        isActiveAdmin(getAdminById(sessionAdminId)); // 슈퍼관리자 활성 상태 검사
        Admin admin = getAdminById(targetId);

        // String으로 받아서 Role.from()으로 치환
        Role newRole = Role.from(roleString);
        admin.updateRole(newRole);
    }

    @Transactional
    public void updateAdminStatus(Long targetId, String statusString, Long sessionAdminId) {
        isActiveAdmin(getAdminById(sessionAdminId));
        Admin admin = getAdminById(targetId);

        AdminStatus newStatus = AdminStatus.from(statusString);
        admin.updateStatus(newStatus);
    }

    @Transactional
    public void deleteAdmin(Long targetId, Long sessionAdminId) {
        isActiveAdmin(getAdminById(sessionAdminId));
        Admin admin = getAdminById(targetId);

        // Soft Delete 로직
        admin.updateStatus(AdminStatus.INACTIVE);
    }

    // 토큰 재발급 로직
    @Transactional
    public String reissueAccessToken(String refreshToken) {
        // 1. 리프레시 토큰 자체의 서명 및 만료일 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new ServiceException(ErrorCode.INVALID_INPUT_VALUE); // "유효하지 않은 토큰입니다."
        }

        // 2. DB에 해당 토큰이 실제로 존재하는지 확인
        RefreshToken savedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ServiceException(ErrorCode.INVALID_INPUT_VALUE));

        String email = savedToken.getEmail();

        // 3. 이메일로 관리자 정보 조회하여 새로운 Access Token 발급
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorCode.ADMIN_NOT_FOUND));

        isActiveAdmin(admin); // 정지되거나 탈퇴한 유저인지 상태 재검증

        return jwtUtil.createToken(admin.getId(), admin.getEmail(), admin.getRole().name());
    }

}
