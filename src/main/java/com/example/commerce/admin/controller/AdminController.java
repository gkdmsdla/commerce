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
        LoginResponse response= adminService.login(request);

        SessionAdmin sessionAdmin = new SessionAdmin(response);
        session.setAttribute("loginAdmin", sessionAdmin);
        session.setMaxInactiveInterval(120);// 자동 로그아웃 후 상태 변경 확인하기 위해 120초 설정

        return CommonResponseHandler.success(SuccessCode.LOGIN_SUCCESSFUL, response);
        //return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 관리자 리스트 조회 (슈퍼 관리자 전용)
    // Spring Security가 세션/토큰을 확인하여 ROLE_SUPER_ADMIN이 아니면 403을 반환.
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping
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
            /*
            defaultValue : 클라이언트가 파라미터를 보내지 않았을 때 기본으로 적용되는 값
            페이징 처리 시 프론트엔드가 번호를 생략하면 1페이지부터 10개씩 보여줌
             */
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Pageable 객체 생성 (Spring Data JPA는 페이지가 0부터 시작하므로 -1 넣었음)
        PageRequest pageable = PageRequest.of(page - 1, size);

        //  서비스 로직 호출하여 데이터 가져오기
        Page<AdminDetailResponse> response = adminService.getAdminList(keyword, role, status, pageable);

        // 200 OK 상태 코드와 함께 데이터 반환
        //return ResponseEntity.ok(response);
        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response);

        // 기본 조회 외의 내용이 있으면 추가
    }

    //수정 필요
    //관리자 1명의 정보 상세조회
    @PreAuthorize("hasRole('CS_ADMIN')")
    @GetMapping("/admins/{id}")
    public ResponseEntity<CommonResponseDTO<GetAdminResponse>> getOne(@PathVariable long id){
        GetAdminResponse response = adminService.getOne(id);
        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response);
    }

    // 그냥 내 정보 조회
    //수정 필요
    @PreAuthorize("hasRole('CS_ADMIN')") // 딱히 필요없을지도,,, 관리자가 로그인 되어있는지 Session 으로 확인하니까
    @GetMapping("/admins/me")
    public ResponseEntity<CommonResponseDTO<GetMyInfoResponse>> getOne(HttpSession session){
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("longinAdmin");
        if (sessionAdmin == null){
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
        }

        GetMyInfoResponse response = adminService.getMyInfo(sessionAdmin.getId());
        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response);
    }

    // 그냥 내 정보 수정
    @PutMapping("/admins/me")
    public ResponseEntity<CommonResponseDTO<UpdateMyInfoResponse>> updateMe(
            @Valid @RequestBody UpdateMyInfoRequest request, HttpSession session){
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("longinAdmin");
        if (sessionAdmin == null){
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
        }

        UpdateMyInfoResponse response = adminService.updateMyInfo(sessionAdmin.getId(), request);
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED, response);
    }



    // 관리자 가입 승인 (슈퍼 관리자 전용)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<CommonResponseDTO<Void>> approveAdmin(@PathVariable Long id) {
        adminService.approveAdmin(id);
        return CommonResponseHandler.success(SuccessCode.STATUS_PATCHED);
    }

    // 관리자 정보 수정 (본인이거나 슈퍼 관리자일 경우)
    // #id는 URL의 {id}를 의미하며, principal.id는 로그인한 사용자의 ID를 의미합니다.
    @PreAuthorize("#id == authentication.principal.id or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponseDTO<UpdateAdminResponse>> updateAdminInfo(@PathVariable Long id, @RequestBody UpdateAdminRequest request) {

        UpdateAdminResponse response = adminService.updateAdminInfo(id, request);
        // ... (서비스 호출)
        //return ResponseEntity.ok().build();
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED,response);
    }

}
