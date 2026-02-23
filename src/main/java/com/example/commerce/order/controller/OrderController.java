package com.example.commerce.order.controller;

import com.example.commerce.global.common.CommonResponseDTO;
import com.example.commerce.global.common.CommonResponseHandler;
import com.example.commerce.global.common.SuccessCode;
import com.example.commerce.order.dto.*;
import lombok.RequiredArgsConstructor;
import com.example.commerce.admin.dto.SessionAdmin;
import com.example.commerce.customer.dto.SessionCustomer;
import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
import com.example.commerce.order.dto.*;
import com.example.commerce.order.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    ResponseEntity<CommonResponseDTO<CreateOrderResponse>> create(
            @Valid @RequestBody CreateOrderRequest request, HttpSession session) {
        //고객이 주문
        // 세션에서 customer 정보를 빼와야됨
        SessionCustomer sessionCustomer = (SessionCustomer) session.getAttribute("loginCustomer");
        if (sessionCustomer == null) {
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
        }

        CreateOrderResponse response = orderService.create(sessionCustomer.getId(), request);

        return CommonResponseHandler.success(SuccessCode.ORDER_SUCCESSFUL, response);
    }

    @PostMapping("/admins/orders")
    ResponseEntity<CommonResponseDTO<CreateAdminOrderResponse>> create(
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


    @GetMapping("/admin/orders") // 해당 endpoint로 get 요청이 들어올 경우 아래 메서드로 응답할 거다.
    public ResponseEntity<Page<GetAllAdminOrderResponse>> getAllByAdmin(@PageableDefault() Pageable pageable, HttpSession session) {

        // 아래 권한 체크
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
        // 세션에서 겟어트리뷰트를 통해서 로그인 어드민 키 값에 해당하는 데이터를 받아올 거고
        // 세션 어드민이라는 dto에 담아서 쓸 거다.
        // dto를 사용할 때는 seesionAdmin을 사용할 거다.
        if (sessionAdmin == null) {
            // 아무런 값이 없으면
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
            // before_login을 던져줄 거다.
        }

        Page<GetAllAdminOrderResponse> response = orderService.getAllByAdmin(pageable);
        // 응답할 데이터 ( Page<GetAllAdminOrderResponse>  ) 를 만들기 위해서,
        //
        //orderService에 있는 getAllByAdmin 이란 메서드를 사용할거다 .
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/orders")
    public ResponseEntity<Page<GetAllCustomerOrderResponse>> getAllbyCustomer() {

        SessionCustomer sessionCustomer = (SessionCustomer) session.getAttribute("loginAdmin");

        return CommonResponseHandler.success(SuccessCode.ORDER_SUCCESSFUL, response);
    }


    // 단건 주문 조회 (관리자)
    @GetMapping("/admins/orders/{id}")
    ResponseEntity<CommonResponseDTO<GetOneAdminOrderResponse>> getOne(
            @PathVariable("id") Long orderId,
            HttpSession session) {

        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
        if (sessionAdmin == null) {
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
        }

        GetOneAdminOrderResponse response = orderService.getOneAdminOrder(orderId, sessionAdmin.getId());

        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response);
    }

    // 단건 주문 조회 (고객)
    @GetMapping("/orders/{id}")
    ResponseEntity<CommonResponseDTO<GetOneOrderResponse>> getOneOrder(
            @PathVariable("id") Long orderId,
            HttpSession session) {

        SessionCustomer sessionCustomer = (SessionCustomer) session.getAttribute("loginCustomer");
        if (sessionCustomer == null) {
            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
        }

        Page<GetAllCustomerOrderResponse> response = orderService.getAllByCustomer(sessionCustomer.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
        GetOneOrderResponse response = orderService.getOneOrder(orderId, sessionCustomer.getId());

        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response);
    }

    // 주문 취소 (관리자)
    @PatchMapping("admins/order/{id}/cancel")
    public



}