package com.example.commerce.customer.controller;

import com.example.commerce.customer.dto.*;
import com.example.commerce.customer.service.CustomerService;
import com.example.commerce.global.common.CommonResponseDTO;
import com.example.commerce.global.common.CommonResponseHandler;
import com.example.commerce.global.common.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    // 1. 회원가입 (메서드 이름 수정: createCustomerResponse -> createCustomer)
    @PostMapping("/signup")
    public ResponseEntity<CommonResponseDTO<CreateCustomerResponse>> signup(
            @Valid @RequestBody CreateCustomerRequest request
    ) {
        CreateCustomerResponse response = customerService.createCustomer(request);
        return CommonResponseHandler.success(SuccessCode.CUSTOMER_SIGNUP, response);
    }

    // 2. 로그인 (JWT 방식으로 전면 수정)
    @PostMapping("/login")
    public ResponseEntity<CommonResponseDTO<LoginCustomerResponse>> login(
            @Valid @RequestBody LoginCustomerRequest request
    ) {
        // 세션 대신, 토큰이 담긴 DTO를 받도록 수정
        LoginCustomerResponse response = customerService.customerLogIn(request);

        // 세션 타임아웃을 이 곳에서 하지 않도록 수정(stateless 한 JWT 방식으로 수정했기 때문)
        return CommonResponseHandler.success(SuccessCode.LOGIN_SUCCESSFUL, response);
    }

    // 3. 고객 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponseDTO<GetOneCustomerResponse>> findCustomer(
            @PathVariable Long id
    ){
        GetOneCustomerResponse response = customerService.findCustomer(id);
        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response);
    }

    // 4. 고객 리스트 조회
    @GetMapping
    public ResponseEntity<CommonResponseDTO<List<GetOneCustomerResponse>>> findAllCustomer(){
        List<GetOneCustomerResponse> responses = customerService.findAllCustomer();
        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, responses);
    }

    // 5. 고객 정보 수정
    @PatchMapping("/{id}")
    public ResponseEntity<CommonResponseDTO<GetOneCustomerResponse>> updateCustomer(
            @PathVariable Long id,
            @RequestBody UpdateCustomerRequest request
    ) {
        GetOneCustomerResponse response = customerService.updateCustomer(id, request);
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED, response);
    }

    // 6. 고객 삭제 (Soft Delete 권장)
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponseDTO<Void>> deleteCustomer(
            @PathVariable Long id
    ) {
        customerService.deleteCustomer(id);
        return CommonResponseHandler.success(SuccessCode.DELETE_SUCCESSFUL, null);
    }
}