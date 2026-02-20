package com.example.commerce.customer.controller;

import com.example.commerce.customer.dto.*;
import com.example.commerce.customer.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // 고객 생성
    @PostMapping
    public CreateCustomerResponse createCustomer(
            @RequestBody @Valid CreateCustomerRequest request
    ) {
        return customerService.createCustomerResponse(request);
    }

    // 로그인
    @PostMapping("/login")
    public String login(
            @RequestBody LoginCustomerRequest request,
            HttpSession session
    ) {
        return customerService.customerLogin(request, session);
    }

    // 로그아웃
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "로그아웃 성공";
    }

    // 고객 단건 조회
    @GetMapping("/{id}")
    public GetOneCustomerResponse findCustomer(@PathVariable Long id) {
        return customerService.findCustomer(id);
    }

    // 고객 전체 조회
    @GetMapping
    public List<GetOneCustomerResponse> findAllCustomer() {
        return customerService.findAllCustomer();
    }

    // 고객 수정
    @PatchMapping("/{id}")
    public GetOneCustomerResponse updateCustomer(
            @PathVariable Long id,
            @RequestBody UpdateCustomerRequest request
    ) {
        return customerService.updateCustomer(id, request);
    }

    // 고객 삭제
    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }
}