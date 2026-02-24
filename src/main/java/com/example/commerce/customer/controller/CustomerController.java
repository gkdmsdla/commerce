package com.example.commerce.customer.controller;

import com.example.commerce.customer.dto.*;
import com.example.commerce.customer.entity.CustomerStatus;
import com.example.commerce.customer.service.CustomerService;
import com.example.commerce.global.common.CommonResponseDTO;
import com.example.commerce.global.common.CommonResponseHandler;
import com.example.commerce.global.common.SuccessCode;
import com.example.commerce.global.security.AdminUserDetails;
import com.example.commerce.global.security.CustomerUserDetails;
import com.example.commerce.global.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    // 1. 회원가입 (메서드 이름 수정: createCustomerResponse -> createCustomer)
    @PostMapping("/signUp")
    public ResponseEntity<CommonResponseDTO<SignupCustomerResponse>> signup(
            @Valid @RequestBody SignupCustomerRequest request
    ) {
        SignupCustomerResponse response = customerService.createCustomer(request);
        return CommonResponseHandler.success(SuccessCode.CUSTOMER_SIGNUP, response);
    }

    // 2. 로그인 (JWT 방식으로 전면 수정)
    @PostMapping("/logIn")
    public ResponseEntity<CommonResponseDTO<LoginCustomerResponse>> login(
            @Valid @RequestBody LoginCustomerRequest request
    ) {
        // 세션 대신, 토큰이 담긴 DTO를 받도록 수정
        LoginCustomerResponse response = customerService.customerLogIn(request);

        // 세션 타임아웃을 이 곳에서 하지 않도록 수정(stateless 한 JWT 방식으로 수정했기 때문)
        return CommonResponseHandler.success(SuccessCode.LOGIN_SUCCESSFUL, response);
    }

    // 3. 고객 상세 조회
    @PreAuthorize("(hasRole ('CUSTOMER') and #id== principal.id)" + " or hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponseDTO<GetOneCustomerResponse>> findCustomer(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
            ){
        GetOneCustomerResponse response = customerService.findCustomer(id, userPrincipal);
        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response);
    }

    // 4. 고객 리스트 조회
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @GetMapping
    public ResponseEntity<CommonResponseDTO<List<GetOneCustomerResponse>>> findAllCustomer(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CustomerStatus status,
            @RequestParam(required = false) String sort,    // 클라이언트가 무엇을 기준으로 줄세울지 알려주는 값
            @RequestParam(required = false) boolean desc,   // true 면 내림차순, false 거나 값이 없으면 오름차순

            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,

            @AuthenticationPrincipal UserPrincipal userPrincipal
    ){
        Sort.Direction direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;

        String sortValue = "name";

        if ("email".equals(sort)) sortValue = "email";
        else if ("createdAt".equals(sort)) sortValue = "createdAt";

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortValue));
        Page<GetOneCustomerResponse> response = customerService.findAllCustomer(userPrincipal, keyword, status, pageable);
        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response.getContent());
    }

    // 5. 고객 정보 수정
    @PreAuthorize("(hasRole ('CUSTOMER') and #id== principal.id)" + " or hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<CommonResponseDTO<GetOneCustomerResponse>> updateCustomer(
            @PathVariable Long id,
            @RequestBody UpdateCustomerRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        GetOneCustomerResponse response = customerService.updateCustomer(id, request, userPrincipal);
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED, response);
    }

    // 6. 고객 삭제 (Soft Delete 권장)
    @PreAuthorize("hasRole ('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponseDTO<Void>> deleteCustomer(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        customerService.deleteCustomer(id, userPrincipal);
        return CommonResponseHandler.success(SuccessCode.DELETE_SUCCESSFUL);
    }

    // 7. 고객 상태 수정
    @PreAuthorize("hasAnyRole ('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<CommonResponseDTO<UpdateCustomerStatusResponse>> updateCustomerStatus(
            @PathVariable Long id,
            @RequestBody UpdateCustomerStatusRequest requset,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ){
        UpdateCustomerStatusResponse response = customerService.updateCustomerStatus(id, requset, userPrincipal);
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED, response);
    }
}