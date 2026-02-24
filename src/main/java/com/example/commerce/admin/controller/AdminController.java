package com.example.commerce.admin.controller;

import com.example.commerce.admin.dto.*;
import com.example.commerce.admin.entity.AdminStatus;
import com.example.commerce.admin.entity.Role;
import com.example.commerce.admin.service.AdminService;
import com.example.commerce.global.common.CommonResponseDTO;
import com.example.commerce.global.common.CommonResponseHandler;
import com.example.commerce.global.common.SuccessCode;
import com.example.commerce.global.security.AdminUserDetails;
import com.example.commerce.global.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins")
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/signup")
    ResponseEntity<CommonResponseDTO<SignupAdminResponse>> signup(
            @Valid @RequestBody SignupAdminRequest request

    ){
        SignupAdminResponse response = adminService.signup(request);

        return CommonResponseHandler.success(SuccessCode.ADMIN_SIGNUP, response);
        //return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponseDTO<LoginAdminResponse>> login(
            @Valid @RequestBody LoginAdminRequest request
    ){
        // 1. HttpServletRequest 파라미터 삭제
        // 2. 서비스 호출 시에도 request(DTO)만 넘김
        LoginAdminResponse response = adminService.login(request);

        // JWT 토큰이 포함된 response 를 Data 로 넣어서 반환
        return CommonResponseHandler.success(SuccessCode.LOGIN_SUCCESSFUL, response);
    }

    // 로그아웃 API 신규 구현
    @PostMapping("/logout")
    public ResponseEntity<CommonResponseDTO<Void>> logout(
            @AuthenticationPrincipal AdminUserDetails userDetails
    ) {
        adminService.logout(userDetails.getAdmin().getId());
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED); // 또는 LOGOUT_SUCCESSFUL 등 상황에 맞는 코드
    }

    // 관리자 리스트 조회 (슈퍼 관리자 전용)
    // Spring Security가 세션/토큰을 확인하여 ROLE_SUPER_ADMIN이 아니면 403을 반환.
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/admins") //endpoint 수정 (by 권지원, at 02/21 12:35)
    public ResponseEntity<CommonResponseDTO<List<GetOneAdminResponse>>> getAdminList(
            /*
            * RequestParam : URL 주소 뒤에 ? 를 붙이고, key=value 형태로 데이터 보내는 쿼리 스트림을
              Java의 변수로 자동 적용해주는 어노테이션
           * required = false -> 검색조건에서 있어도, 없어도 상관없다면 false 로 되어있어야 에러 방지됨
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

            // 정렬 기준 추가했습니당
            // request param 이름 -> sort
            // sort 가 null 이거나 기준을 못찾겠다 싶으면 role ASC 로 출력하도록 했고
            // email 이나 createdAt 으로도 출력되게 했어요
            //@RequestParam(required = false) String sort,
            // 오름차순으로 할지, 내림차순으로 할지도 추가하겠습니다~
            // request param 이름 -> desc
            // boolean 값으로 받아서 false or NULL -> 오름차순
            // true 이면 내림차순으로 출력할게요!
            //@RequestParam(required = false) boolean desc,

            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        //SessionAdmin 리팩터링 (과제 3 내 정렬기준, 순서 기능 추가 및 최적화)
        //Session 공통 추출 메서드는 따로 만들어서 제일 아래에 두었습니다!
//        SessionAdmin sessionAdmin = getSessionAdmin(session);

        //오름차순(ASC) 으로 할지 내림차순(DESC) 로 할지 결정
        Sort.Direction direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;

        // 기본설정은 Role(직책) 기준으로 정렬
        String sortValue = "role";

        // 만일 클라이언트가 email 이나 생성순서(CreatedAt) 기준 정렬 요청 시 정렬 기준 변경
        if ("email".equals(sort)) sortValue = "email";
        else if ("createdAt".equals(sort)) sortValue = "createdAt";

        // 페이지 번호와 크기만 있던 기존 코드에서 정렬 기준과 오름/내림차순 받을 수 있게 변경
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortValue));
        Page<GetOneAdminResponse> response = adminService.getAdminList(userPrincipal, keyword, role, status, pageable);

        // 200 OK 상태 코드와 함께 데이터 반환
        //return ResponseEntity.ok(response);
        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response.getContent());

        // 기본 조회 외의 내용이 있으면 추가
    }

    //수정 필요
    //관리자 1명의 정보 상세조회
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @GetMapping("/{adminId}")
    public ResponseEntity<CommonResponseDTO<GetOneAdminResponse>> getOne(
            @PathVariable long adminId, @AuthenticationPrincipal UserPrincipal userPrincipal){
//        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
//        if (sessionAdmin == null) {
//            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
//        }

        GetOneAdminResponse response = adminService.getAdminDetail(adminId, userPrincipal);
        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response);
    }

    // 그냥 내 정보 조회 (중복 제거 및 AdminDetailResponse 로 통합) -> getMyInfo 삭제
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<CommonResponseDTO<GetOneAdminResponse>> getMe(
            @AuthenticationPrincipal UserPrincipal userPrincipal){
        Long myId = userPrincipal.getId();
        // 타인 조회 로직에 내 ID를 넣어서 리팩터링
        GetOneAdminResponse response = adminService.getAdminDetail(myId, userPrincipal);
        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response);
    }

    // 그냥 내 정보 수정 (중복 제거 및 UpdateAdminRequest 로 통합) -> updateMyInfo 삭제
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @PutMapping("/me")
    public ResponseEntity<CommonResponseDTO<UpdateAdminResponse>> updateMe(
            @Valid @RequestBody UpdateAdminRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal){
        Long myId = userPrincipal.getId();
        // 타인 수정 로직에 내 ID를 넣어서 리팩터링
        UpdateAdminResponse response = adminService.updateAdminInfo(myId, request, userPrincipal);
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED, response);
    }



    // 관리자 가입 승인 (슈퍼 관리자 전용)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/{adminId}/approve")
    public ResponseEntity<CommonResponseDTO<Void>> approveAdmin(
            @PathVariable Long adminId, @AuthenticationPrincipal AdminUserDetails userDetails) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        System.out.println(">>> current auth: " + auth);
//        System.out.println(">>> authorities: " + auth.getAuthorities());
//        System.out.println(">>> principal: " + auth.getPrincipal());
        //관리자 로그인 확인
//        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
//        if (sessionAdmin == null){
//            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
//        }

        adminService.approveAdmin(adminId, userDetails.getAdmin().getId());
        return CommonResponseHandler.success(SuccessCode.STATUS_PATCHED);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/{adminId}/reject")
    public ResponseEntity<CommonResponseDTO<RejectResponse>> rejectAdmin(
            @PathVariable Long adminId, @Valid @RequestBody RejectRequest request,
            @AuthenticationPrincipal AdminUserDetails userDetails) {
//        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
//        if (sessionAdmin == null){
//            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
//        }

        RejectResponse response = adminService.rejectAdmin(adminId, request, userDetails.getAdmin().getId());
        //string 을 빼서 사용하는건 service 한테 맡겼습니다~
        return CommonResponseHandler.success(SuccessCode.STATUS_PATCHED, response);
    }

    // 관리자 정보 수정 (본인이거나 슈퍼 관리자일 경우)
    // #id는 URL의 {id}를 의미하며, principal.id는 로그인한 사용자의 ID를 의미합니다.
    @PreAuthorize("#adminId == principal.id or hasRole('SUPER_ADMIN')")
    @PutMapping("/{adminId}")
    public ResponseEntity<CommonResponseDTO<UpdateAdminResponse>> updateAdminInfo(
            @PathVariable Long adminId, @RequestBody UpdateAdminRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
//        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
//        //오탈자 수정
//        if (sessionAdmin == null){
//            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
//        }

        UpdateAdminResponse response = adminService.updateAdminInfo(adminId, request, userPrincipal);
        // ... (서비스 호출) -> 수정했습니다~
        //return ResponseEntity.ok().build();
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED,response);
    }

    // =================================================================
    // [신규 구현] 관리자 권한/상태 제어 및 삭제
    // =================================================================

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{adminId}/status")
    public ResponseEntity<CommonResponseDTO<Void>> updateAdminStatus(
            @PathVariable Long adminId, @Valid @RequestBody UpdateAdminStatusRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        //SessionAdmin sessionAdmin = getSessionAdmin(session);
        adminService.updateAdminStatus(adminId, request.getStatus(), userPrincipal);
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{adminId}")
    public ResponseEntity<CommonResponseDTO<Void>> deleteAdmin(
            @PathVariable Long adminId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        //SessionAdmin sessionAdmin = getSessionAdmin(session);
        adminService.deleteAdmin(adminId, userPrincipal);
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{adminId}/role")
    public ResponseEntity<CommonResponseDTO<Void>> updateAdminRole(
            @PathVariable Long adminId, @Valid @RequestBody UpdateRoleRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        //SessionAdmin sessionAdmin = getSessionAdmin(session);
        // (주의: Service 에도 updateAdminRole 메서드가 존재해야 합니다)
        adminService.updateAdminRole(adminId, request.getRole(), userPrincipal);
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED);
    }

    @PostMapping("/reissue")
    public ResponseEntity<CommonResponseDTO<String>> reissue(
            // 헤더의 "Refresh-Token" 키값으로 받는다고 가정
            //(같이 공부할 포인트) 실무 서비스에서는 프론트엔드가 Header 나 Body에 리프레시 토큰을 담아 보낸다 함.
            @RequestHeader("Refresh-Token") String refreshToken
    ) {
        // 서비스 호출하여 새로운 Access Token 발급
        String newAccessToken = adminService.reissueAccessToken(refreshToken);

        // 새 토큰을 응답으로 반환
        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, newAccessToken);
    }
}