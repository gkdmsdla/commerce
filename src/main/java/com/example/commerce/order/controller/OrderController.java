package com.example.commerce.order.controller;

import com.example.commerce.order.dto.*;
import lombok.RequiredArgsConstructor;
import com.example.commerce.admin.dto.SessionAdmin;
import com.example.commerce.customer.dto.SessionCustomer;
import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
import com.example.commerce.order.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
    ResponseEntity<CreateAdminOrderResponse> create(
            @Valid @RequestBody CreateAdminOrderRequest request, HttpSession session) {
        //관리자 주문
        // 세션에서 admin 정보를 빼와야됨

        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
        if (sessionAdmin == null) {
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
        }

        CreateAdminOrderResponse response = orderService.createByAdmin(sessionAdmin.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // 단건 주문 조회 (관리자)
    @GetMapping("admin/orders/{id}")
    ResponseEntity<GetOneAdminOrderResponse> getOne(
            @PathVariable("id") Long orderId,
            HttpSession session) {

        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
        if (sessionAdmin == null) {
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
        }

        GetOneAdminOrderResponse response = orderService.getOneAdminOrder(orderId, sessionAdmin.getId());

        return ResponseEntity.ok(response);
    }

    // 단건 주문 조회 (고객)
    @GetMapping("orders/{id}")
    ResponseEntity<GetOneOrderResponse> getOneOrder(
            @PathVariable("id") Long orderId,
            HttpSession session) {

        SessionCustomer sessionCustomer = (SessionCustomer) session.getAttribute("loginCustomer");
        if (sessionCustomer == null) {
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
        }

        GetOneOrderResponse response = orderService.getOneOrder(orderId, sessionCustomer.getId());

        return ResponseEntity.ok(response);

    }
}