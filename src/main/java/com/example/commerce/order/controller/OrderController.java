package com.example.commerce.order.controller;

import com.example.commerce.global.common.CommonResponseDTO;
import com.example.commerce.global.common.CommonResponseHandler;
import com.example.commerce.global.common.SuccessCode;
import com.example.commerce.global.security.UserPrincipal;
import com.example.commerce.order.dto.*;
import com.example.commerce.order.entity.OrderStatus;
import lombok.RequiredArgsConstructor;
import com.example.commerce.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;

    // 1. 고객의 주문 생성
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/orders")
    ResponseEntity<CommonResponseDTO<CreateOrderResponse>> create(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        //고객이 주문
        // 세션에서 customer 정보를 빼와야됨
//        SessionCustomer sessionCustomer = (SessionCustomer) session.getAttribute("loginCustomer");
//        if (sessionCustomer == null) {
//            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
//        }

        CreateOrderResponse response = orderService.create(userPrincipal, request);

        return CommonResponseHandler.success(SuccessCode.ORDER_SUCCESSFUL, response);
    }

    //2. 관리자의 주문 생성
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @PostMapping("/admins/orders")
    ResponseEntity<CommonResponseDTO<CreateOrderByAdminResponse>> create(
            @Valid @RequestBody CreateOrderByAdminRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        //관리자 주문
        // 세션에서 admin 정보를 빼와야됨

//        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
//        if (sessionAdmin == null) {
//            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
//        }

        CreateOrderByAdminResponse response = orderService.createByAdmin(userPrincipal, request);
        return CommonResponseHandler.success(SuccessCode.ORDER_SUCCESSFUL, response);
    }


    // 관리자의 주문 전체 조회
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @GetMapping("/admin/orders") // 해당 endpoint로 get 요청이 들어올 경우 아래 메서드로 응답할 거다.
    public ResponseEntity<CommonResponseDTO<List<GetOrdersByAdminResponse>>> getAllByAdmin(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault() Pageable pageable,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {


        Page<GetOrdersByAdminResponse> response = orderService.getAllByAdmin(keyword, status, pageable, userPrincipal);
        // 응답할 데이터 ( Page<GetAllAdminOrderResponse>  ) 를 만들기 위해서,
        //
        //orderService에 있는 getAllByAdmin 이란 메서드를 사용할거다 .
        return CommonResponseHandler.success(SuccessCode.ORDER_SUCCESSFUL, response.getContent());
    }

    // 고객의 주문 전체 조회
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/orders")
    public ResponseEntity<CommonResponseDTO<List<GetOrdersResponse>>> getAllbyCustomer(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) OrderStatus status,

            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,

            @RequestParam(required = false) String sort,
            @RequestParam(required = false) boolean desc,

            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Sort.Direction direction = (desc) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortValue = "email";
        if ("price".equals(sort)) sortValue = "price";
        else if ("createdAt".equals(sort)) sortValue = "createdAt";

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortValue));

        Page<GetOrdersResponse> response = orderService.getAllByCustomer(userPrincipal, keyword, status, pageable);

        return CommonResponseHandler.success(SuccessCode.ORDER_SUCCESSFUL, response.getContent());
    }


    // 관리자의 주문 단건 조회
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @GetMapping("/admins/orders/{id}")
    ResponseEntity<CommonResponseDTO<GetOneOrderByAdminResponse>> getOne(
            @PathVariable("id") Long orderId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

//        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
//        if (sessionAdmin == null) {
//            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
//        }

        GetOneOrderByAdminResponse response = orderService.getOneAdminOrder(orderId, userPrincipal);

        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response);
    }

    // 고객의 주문 단건 조회
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/orders/{id}")
    ResponseEntity<CommonResponseDTO<GetOneOrderResponse>> getOneOrder(
            @PathVariable("id") Long orderId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

//        SessionCustomer sessionCustomer = (SessionCustomer) session.getAttribute("loginCustomer");
//        if (sessionCustomer == null) {
//            throw new ServiceException(ErrorCode.BEFORE_LOGIN);
//        }
        GetOneOrderResponse response = orderService.getOneOrder(orderId, userPrincipal);

        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response);
    }


    //주문 취소
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/admins/order/{id}/cancel")
    ResponseEntity<CommonResponseDTO<CancelOrderResponse>> getCancel(
            @PathVariable("id") Long orderId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid CancelOrderRequest request) {

        CancelOrderResponse response = orderService.cancelByAdmin(orderId, userPrincipal, request);

        return CommonResponseHandler.success(SuccessCode.DELETE_SUCCESSFUL, response);
    }

    // 주문 완료
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @PatchMapping("/admins/orders/{orderId}/delivered")
    ResponseEntity<CommonResponseDTO<String>> deliverCompleted(
            @PathVariable Long orderId, @AuthenticationPrincipal UserPrincipal userPrincipal
    ){
        orderService.deliverCompleted(orderId, userPrincipal);

        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED, "배달이 완료되었습니다.");
    }
}