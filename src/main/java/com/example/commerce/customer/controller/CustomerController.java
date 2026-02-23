package com.example.commerce.customer.controller;

import com.example.commerce.customer.dto.*;
import com.example.commerce.customer.entity.CustomerStatus;
import com.example.commerce.customer.service.CustomerService;
import com.example.commerce.global.common.CommonResponseDTO;
import com.example.commerce.global.common.CommonResponseHandler;
import com.example.commerce.global.common.SuccessCode;
import com.example.commerce.global.security.AdminUserDetails;
import jakarta.servlet.http.HttpSession;
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

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<CommonResponseDTO<SignupCustomerResponse>> signup(
            @Valid @RequestBody SignupCustomerRequest request
    ) {
        SignupCustomerResponse response = customerService.customerSignUp(request);

        return CommonResponseHandler.success(SuccessCode.CUSTOMER_SIGNUP, response);
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<CommonResponseDTO<LoginCustomerResponse>> login(
            @Valid @RequestBody LoginCustomerRequest request,
            HttpSession httpSession) {
        LoginCustomerResponse response = customerService.customerLogIn(request, httpSession);
        //session.setMaxInactiveInterval(120);
        return CommonResponseHandler.success(SuccessCode.LOGIN_SUCCESSFUL, response);

    }

    // 고객 상세 조회
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponseDTO<GetOneCustomerResponse>> findCustomer(
            @PathVariable Long id
    ){
        GetOneCustomerResponse response = customerService.findCustomer(id);

        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response);
    }

    // 고객 리스트 조회

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @GetMapping
    public ResponseEntity<CommonResponseDTO<List<GetOneCustomerResponse>>> findAllCustomer(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CustomerStatus status,
            @RequestParam(required = false) String sort,    // 클라이언트가 무엇을 기준으로 줄세울지 알려주는 값
            @RequestParam(required = false) boolean desc,   // true 면 내림차순, false 거나 값이 없으면 오름차순

            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,

            @AuthenticationPrincipal AdminUserDetails userDetails
    ){
        Sort.Direction direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;

        String sortValue = "name";

        if ("email".equals(sort)) sortValue = "email";
        else if ("createdAt".equals(sort)) sortValue = "createdAt";

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortValue));
        Page<GetOneCustomerResponse> response = customerService.findAllCustomer(userDetails.getAdmin().getId(), keyword, status, pageable);

        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response.getContent());
    }

    // 고객 정보 수정
    // 여기 관리자 말고 로그인한 본인도 할 수 있도록 수정해야함!

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<CommonResponseDTO<GetOneCustomerResponse>> updateCustomer(
            @PathVariable Long id,
            @RequestBody UpdateCustomerRequest request
    ) {
        GetOneCustomerResponse response = customerService.updateCustomer(id, request);
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED, response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponseDTO<Void>> deleteCustomer(
            @PathVariable Long id
    ) {
        customerService.deleteCustomer(id);
        return CommonResponseHandler.success(SuccessCode.DELETE_SUCCESSFUL, null);
    }

    // 고객 상태 수정

    @PatchMapping("/{id}/status")
    public ResponseEntity<CommonResponseDTO<UpdateCustomerStatusResponse>> updateCustomerStatus(
            @PathVariable Long id,
            @RequestBody UpdateCustomerStatusRequest requset
    ){
        UpdateCustomerStatusResponse response = customerService.updateCustomerStatus(id, requset);
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED, response);
    }
}