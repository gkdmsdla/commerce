package com.example.commerce.admin.controller;

import com.example.commerce.admin.dto.*;
import com.example.commerce.admin.entity.AdminStatus;
import com.example.commerce.admin.entity.Role;
import com.example.commerce.admin.service.AdminService;
import com.example.commerce.global.common.CommonResponseDTO;
import com.example.commerce.global.common.CommonResponseHandler;
import com.example.commerce.global.common.SuccessCode;
import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/signup")
    ResponseEntity<CommonResponseDTO<SignupResponse>> signup(@Valid @RequestBody SignupRequest request){
        SignupResponse response = adminService.signup(request);

        return CommonResponseHandler.success(SuccessCode.ADMIN_SIGNUP, response);
        //return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    ResponseEntity<CommonResponseDTO<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session
    ){
        //response 를 받아와서
        LoginResponse response= adminService.login(request);

        //controller 에서 sessionAdmin 조립, 등록
        SessionAdmin sessionAdmin = new SessionAdmin(response);
        session.setAttribute("loginAdmin", sessionAdmin);
        session.setMaxInactiveInterval(120);// 자동 로그아웃 후 상태 변경 확인하기 위해 120초 설정

        //받아온 response 를 Data 로 넣어서 반환
        return CommonResponseHandler.success(SuccessCode.LOGIN_SUCCESSFUL, response);
        //return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 관리자 리스트 조회 (슈퍼 관리자 전용)
    // Spring Security가 세션/토큰을 확인하여 ROLE_SUPER_ADMIN이 아니면 403을 반환.
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/admins") //endpoint 수정 (by 권지원, at 02/21 12:35)
    public ResponseEntity<CommonResponseDTO<Page<AdminDetailResponse>>> getAdminList(
            /*
            * RequestParam : URL 주소 뒤에 ? 를 붙이고, key=value 형태로 데이터 보내는 쿼리 스트림을
              Java의 변수로 자동 적용해주는 어노테이션
           *  required = false -> 검색조건에서 있어도, 없어도 상관없다면 false 로 되어있어야 에러 방지됨
            (기본적으로 RequestParam 값은 클라이언트가 무조건 보내야 하기 때문)
             */
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) AdminStatus status,
            @RequestParam(required = false) String sort,    // 클라이언트가 무엇을 기준으로 줄세울지 알려주는 값
            @RequestParam(required = false) boolean desc,   // true 면 내림차순, false 거나 값이 없으면 오름차순
            /*
            defaultValue : 클라이언트가 파라미터를 보내지 않았을 때 기본으로 적용되는 값
            페이징 처리 시 프론트엔드가 번호를 생략하면 1페이지부터 10개씩 보여줌
             */
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,

            HttpSession session
    ) {
        //SessionAdmin 리팩터링 (과제 3 내 정렬기준, 순서 기능 추가 및 최적화)
        //Session 공통 추출 메서드는 따로 만들어서 제일 아래에 두었습니다!
        SessionAdmin sessionAdmin = getSessionAdmin(session);

        //오름차순(ASC) 으로 할지 내림차순(DESC) 로 할지 결정
        Sort.Direction direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;

        // 기본설정은 Role(직책) 기준으로 정렬
        String sortValue = "role";

        // 만일 클라이언트가 email 이나 생성순서(CreatedAt) 기준 정렬 요청 시 정렬 기준 변경
        if ("email".equals(sort)) sortValue = "email";
        else if ("createdAt".equals(sort)) sortValue = "createdAt";

        // 페이지 번호와 크기만 있던 기존 코드에서 정렬 기준과 오름/내림차순 받을 수 있게 변경
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortValue));
        Page<AdminDetailResponse> response = adminService.getAdminList(sessionAdmin.getId(), keyword, role, status, pageable);

        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response);
    }

    //관리자 1명의 정보 상세조회
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @GetMapping("/admins/{id}")
    public ResponseEntity<CommonResponseDTO<AdminDetailResponse>> getOne(
            @PathVariable long id, HttpSession session){
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
        if (sessionAdmin == null) {
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
        }

        AdminDetailResponse response = adminService.getAdminDetail(id, sessionAdmin.getId());
        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response);
    }

    // 그냥 내 정보 조회
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @GetMapping("/admins/me")
    public ResponseEntity<CommonResponseDTO<GetMyInfoResponse>> getOne(HttpSession session){
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
        if (sessionAdmin == null){
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
        }

        GetMyInfoResponse response = adminService.getMyInfo(sessionAdmin.getId());
        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response);
    }

    // 그냥 내 정보 수정
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @PutMapping("/admins/me")
    public ResponseEntity<CommonResponseDTO<UpdateMyInfoResponse>> updateMe(
            @Valid @RequestBody UpdateMyInfoRequest request, HttpSession session){
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
        if (sessionAdmin == null){
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
        }

        UpdateMyInfoResponse response = adminService.updateMyInfo(sessionAdmin.getId(), request);
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED, response);
    }



    // 관리자 가입 승인 (슈퍼 관리자 전용)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<CommonResponseDTO<Void>> approveAdmin(
            @PathVariable Long id, HttpSession session) {
        //관리자 로그인 확인
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
        if (sessionAdmin == null){
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
        }

        adminService.approveAdmin(id, sessionAdmin.getId());
        return CommonResponseHandler.success(SuccessCode.STATUS_PATCHED);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<CommonResponseDTO<RejectResponse>> rejectAdmin(
            @PathVariable Long id, @Valid @RequestBody RejectRequest request, HttpSession session) {
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
        if (sessionAdmin == null){
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
        }

        RejectResponse response = adminService.rejectAdmin(id, request, sessionAdmin.getId());
        //string 을 빼서 사용하는건 service 한테 맡겼습니다~
        return CommonResponseHandler.success(SuccessCode.STATUS_PATCHED, response);
    }

    // 관리자 정보 수정 (본인이거나 슈퍼 관리자일 경우)
    // #id는 URL의 {id}를 의미하며, principal.id는 로그인한 사용자의 ID를 의미합니다.
    @PreAuthorize("#id == authentication.principal.id or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponseDTO<UpdateAdminResponse>> updateAdminInfo(
            @PathVariable Long id, @RequestBody UpdateAdminRequest request,
            HttpSession session) {
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
        //오탈자 수정
        if (sessionAdmin == null){
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
        }

        UpdateAdminResponse response = adminService.updateAdminInfo(id, request,sessionAdmin.getId());
        // ... (서비스 호출) -> 수정했습니다~
        //return ResponseEntity.ok().build();
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED,response);
    }

    // =================================================================
    // [신규 구현] 관리자 권한/상태 제어 및 삭제
    // =================================================================

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<CommonResponseDTO<Void>> updateAdminStatus(
            @PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request, HttpSession session) {
        SessionAdmin sessionAdmin = getSessionAdmin(session);
        adminService.updateAdminStatus(id, request.getStatus(), sessionAdmin.getId());
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponseDTO<Void>> deleteAdmin(
            @PathVariable Long id, HttpSession session) {
        SessionAdmin sessionAdmin = getSessionAdmin(session);
        adminService.deleteAdmin(id, sessionAdmin.getId());
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<CommonResponseDTO<Void>> updateAdminRole(
            @PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request, HttpSession session) {
        SessionAdmin sessionAdmin = getSessionAdmin(session);
        // (주의: Service 에도 updateAdminRole 메서드가 존재해야 합니다)
        adminService.updateAdminRole(id, request.getRole(), sessionAdmin.getId());
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED);
    }

    // =================================================================
    // [유틸리티] 세션 추출 공통 메서드
    // =================================================================
    private SessionAdmin getSessionAdmin(HttpSession session) {
        // 기존 코드의 "longinAdmin" 오타를 수정하고, 반복되는 예외 처리 로직을 통합
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
        if (sessionAdmin == null) {
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
        }
        return sessionAdmin;
    }
}
