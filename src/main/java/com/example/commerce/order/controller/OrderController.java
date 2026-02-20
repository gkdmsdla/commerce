package com.example.commerce.order.controller;


import com.example.commerce.admin.dto.SessionAdmin;
import com.example.commerce.customer.dto.SessionCustomer;
import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
import com.example.commerce.order.dto.*;
import com.example.commerce.order.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    ResponseEntity<CreateOrderResponse> create(
            @Valid @RequestBody CreateOrderRequest request, HttpSession session) {
        //고객이 주문
        // 세션에서 customer 정보를 빼와야됨
        SessionCustomer sessionCustomer = (SessionCustomer) session.getAttribute("loginCustomer");
        if (sessionCustomer == null) {
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
        }

        CreateOrderResponse response = orderService.create(sessionCustomer.getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("admin/orders")
    ResponseEntity<CreateAdminOrderResponse> create(@Valid @RequestBody CreateAdminOrderRequest request, HttpSession session) {
        //관리자 주문
        // 세션에서 admin 정보를 빼와야됨

        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
        if (sessionAdmin == null) {
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
        }

        CreateAdminOrderResponse response = orderService.createByAdmin(sessionAdmin.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/admin/orders")
    ResponseEntity<GetAllAdminOrderResponse> getAll(
            @RequestParam GetAllAdminOrderRequest request, HttpSession session) {
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
        if (sessionAdmin == null) {
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
        }

        GetAllAdminOrderResponse response = orderService.getByAdmin(sessionAdmin.getId(), request);
        return ResponseEntity.ok(response);
    }
}
