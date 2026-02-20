package com.example.commerce.customer.controller;

import com.example.commerce.customer.dto.*;
import com.example.commerce.customer.service.CustomerService;
import com.example.commerce.global.common.CommonResponseDTO;
import com.example.commerce.global.common.CommonResponseHandler;
import com.example.commerce.global.common.SuccessCode;
import jakarta.servlet.http.HttpSession;
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

    // 고객 회원가입
    @PostMapping("/signup")
    public ResponseEntity<CommonResponseDTO<CreateCustomerResponse>> signup(
            @Valid @RequestBody CreateCustomerRequest request
    ) {
        CreateCustomerResponse response =
                customerService.createCustomerResponse(request);

        return CommonResponseHandler.success(
                SuccessCode.CUSTOMER_SIGNUP,
                response
        );
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<CommonResponseDTO<String>> login(
            @Valid @RequestBody LoginCustomerRequest request,
            HttpSession session
    ) {
        String message = customerService.customerLogin(request, session);

        session.setMaxInactiveInterval(120);

        return CommonResponseHandler.success(
                SuccessCode.LOGIN_SUCCESSFUL,
                message
        );
    }

    // 고객 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponseDTO<GetOneCustomerResponse>> findCustomer(
            @PathVariable Long id
    ) {
        GetOneCustomerResponse response =
                customerService.findCustomer(id);

        return CommonResponseHandler.success(
                SuccessCode.GET_SUCCESSFUL,
                response
        );
    }

    // 전체 조회
    @GetMapping
    public CommonResponseDTO<List<GetOneCustomerResponse>> findAllCustomer() {
        List<GetOneCustomerResponse> response =
                customerService.findAllCustomer();

        return CommonResponseHandler.success(
                SuccessCode.SUCCESS,
                response
        );
    }

    // 수정
    @PatchMapping("/{id}")
    public CommonResponseDTO<GetOneCustomerResponse> updateCustomer(
            @PathVariable Long id,
            @RequestBody UpdateCustomerRequest request
    ) {
        GetOneCustomerResponse response =
                customerService.updateCustomer(id, request);

        return CommonResponseHandler.success(
                SuccessCode.SUCCESS,
                response
        );
    }

    // 삭제
    @DeleteMapping("/{id}")
    public CommonResponseDTO<Void> deleteCustomer(
            @PathVariable Long id
    ) {
        customerService.deleteCustomer(id);

        return CommonResponseHandler.success(
                SuccessCode.SUCCESS,
                null
        );
    }
}