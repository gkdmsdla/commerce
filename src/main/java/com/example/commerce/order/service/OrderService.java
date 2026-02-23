package com.example.commerce.order.service;


import com.example.commerce.admin.entity.Admin;
import com.example.commerce.admin.repository.AdminRepository;
import com.example.commerce.customer.entity.Customer;
import com.example.commerce.customer.repository.CustomerRepository;
import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.order.dto.*;
import com.example.commerce.order.entity.Order;
import com.example.commerce.order.entity.OrderStatus;
import com.example.commerce.order.repository.OrderRepository;
import com.example.commerce.product.entity.Product;
import com.example.commerce.global.exception.ServiceException;
import com.example.commerce.product.entity.ProductStatus;
import com.example.commerce.product.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final AdminRepository adminRepository;


    // 주문 생성
    @Transactional
    public CreateOrderResponse create(long sessionCustomerId, @Valid CreateOrderRequest request) {
        // 세션에 저장되어있는 id 를 기반으로
        // customer repostiory 에서 customer 를 찾음 (없으면 오류 반환)
        Customer customer = customerRepository.findById(sessionCustomerId)
                .orElseThrow(() -> new ServiceException(ErrorCode.CUSTOMER_NOT_FOUND));

        // 상품이 정말로 존재하는지 (없으면 오류 반환)
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ServiceException(ErrorCode.PRODUCT_NOT_FOUND));

        // 재고가 남아있는지 확인
        if (request.getQuantity() > product.getStock()) {
            throw new ServiceException(ErrorCode.SHORT_STOCK);
        }

        Order order = new Order(
                request.getQuantity(),
                calculateTotalPrice(request.getQuantity(), product.getPrice()),
                product,
                customer,
                null
        );

        Order newOrder = orderRepository.save(order);
        product.updateStock(product.getStock() - request.getQuantity());

        return new CreateOrderResponse(
                newOrder.getId(),
                newOrder.getOrderNo(),
                newOrder.getProduct().getName(),
                newOrder.getProduct().getPrice(),
                newOrder.getQuantity(),
                newOrder.getTotalPrice(),
                OrderStatus.PREPARING.getStatusName(),
                newOrder.getCreatedAt()
        );
    }

    @Transactional
    public CreateAdminOrderResponse createByAdmin(Long sessionAdminId, CreateAdminOrderRequest request) {
        // 관리자가 존재하는지
        Admin admin = getAdminById(sessionAdminId);
        isActiveAdmin(admin);

        // 관리자가 주문 관련 자격이 있는지 ...
        // 추가 필요

        // 요청한 고객이 존재하는지
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ServiceException(ErrorCode.CUSTOMER_NOT_FOUND));

        // 상품이 정말로 존재하는지
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ServiceException(ErrorCode.PRODUCT_NOT_FOUND));

        // 재고가 남아있는지 확인
        if (request.getQuantity() > product.getStock()) {
            throw new ServiceException(ErrorCode.SHORT_STOCK);
        }

        Order order = new Order(
                request.getQuantity(),
                calculateTotalPrice(request.getQuantity(), product.getPrice()),
                product,
                customer,
                admin
        );

        Order newOrder = orderRepository.save(order);
        product.updateStock(product.getStock() - request.getQuantity());

        return new CreateAdminOrderResponse(
                newOrder.getId(),
                newOrder.getOrderNo(),

                newOrder.getCustomer().getId(),
                newOrder.getCustomer().getName(),

                newOrder.getProduct().getName(),
                newOrder.getProduct().getPrice(),

                newOrder.getQuantity(),
                newOrder.getTotalPrice(),

                OrderStatus.PREPARING.getStatusName(),
                newOrder.getCreatedAt(),

                newOrder.getAdmin().getName(),
                newOrder.getAdmin().getEmail(),
                newOrder.getAdmin().getRole().getRoleName()
        );
    }

    public long calculateTotalPrice(int quantity, int price) {
        return (long) quantity *price;
    }


    public Page<GetAllAdminOrderResponse> getAllByAdmin(Pageable pageable ){
        Page<Order> orders = orderRepository.findOrders(pageable);
        List<GetAllAdminOrderResponse> dtos = new ArrayList<>();
        // -1 을 조회할 수 없게 예외 처리

        for (Order order : orders) {
            GetAllAdminOrderResponse dto = new GetAllAdminOrderResponse(
                    order.getId(),
                    order.getOrderNo(),
                    order.getCustomer().getName(),
                    order.getProduct().getName(),
                    order.getTotalPrice(),
                    order.getOrderStatus().getStatusName(),
                    order.getQuantity(),
                    order.getCreatedAt(),
                    order.getAdmin().getName()
            );
            dtos.add(dto);
        }
        return new PageImpl<>(dtos, pageable, orders.getTotalElements());
    }


    public Page<GetAllCustomerOrderResponse> getAllByCustomer(String keyword, OrderStatus status, Pageable pageable){
        Page<Order> orders = orderRepository.findOrders(pageable);

        return orders.map(order -> new GetAllCustomerOrderResponse(
                order.getOrderNo(),
                order.getCustomer().getName(),
                order.getProduct().getName(),
                order.getOrderStatus().getStatusName()
        ));
    }


    // 수정을 수정 중입니다:p
//    public UpdateAdminOrderResponse UAOR(UpdateAdminOrderRequest request){
//        Admin admin = adminRepository.findById(sessionAdminId).orElseThrow(
//                ()-> new ServiceException(ErrorCode.ADMIN_NOT_FOUND)
//        );
//
//        Customer customer = customerRepository.findCustomerById(request.getCustomerId()); //orElseThrow
//
//        Product product = productRepository.findProductById(request.getProductId());
//
//        Order order = new Order(
//                request.getQuantity(),
//                calculateTotalPrice(request.getQuantity(), product.getPrice()),
//                product,
//                customer,
//                null
//        );
//
//        Order newOrder = orderRepository.save(order);
//
//        return new CreateOrderResponse(
//                newOrder.getId(),
//                newOrder.getOrderNo(),
//                newOrder.getProduct().getName(),
//                newOrder.getProduct().getPrice(),
//                newOrder.getQuantity(),
//                newOrder.getTotalPrice(),
//                OrderStatus.PREPARING,
//                newOrder.getCreatedAt()
//        );
//    }
//
//
//        return (long) quantity * price;
//    }

    // 주문 단 건 조회 (관리자용)
    public GetOneAdminOrderResponse getOneAdminOrder(Long orderId, Long sessionAdminId) {

        // 관리자가 활성 상태인지 확인 (관리자 맞는지 따로 확인 안해도 되는지 체크하기)
        isActiveAdmin(getAdminById(sessionAdminId));

        // 조회하고자 하는 주문이 정말로 존재하는지 (없으면 오류 반환)
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ORDERING_NOT_FOUND));


        //Order newOrder = orderRepository.save(order);

        return new GetOneAdminOrderResponse(
                order.getOrderNo(),
                order.getQuantity(),
                OrderStatus.PREPARING.getStatusName(),
                order.getCustomer().getName(),
                order.getCustomer().getEmail(),
                order.getProduct().getName(),
                order.getProduct().getPrice(),
                order.getCreatedAt(),
                order.getAdmin().getName(),
                order.getAdmin().getEmail(),
                order.getAdmin().getRole()
        );
    }

    // 주문 단 건 조회 (고객용)
    public GetOneOrderResponse getOneOrder(Long orderId, Long sessionCustomerId) {

        // 조회하고자 하는 주문이 정말로 존재하는지 (없으면 오류 반환)
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ORDERING_NOT_FOUND));

        // 고객 로그인 체크
        if (sessionCustomerId == null) {
            throw new ServiceException(ErrorCode.CUSTOMER_MISMATCH);
        }

        // Order newOrder = orderRepository.save(order);

        return new GetOneOrderResponse(
                order.getOrderNo(),
                order.getQuantity(),
                OrderStatus.PREPARING.getStatusName(),
                order.getCustomer().getName(),
                order.getCustomer().getEmail(),
                order.getProduct().getName(),
                order.getProduct().getPrice(),
                order.getCreatedAt()
        );
    }

    public Admin getAdminById(long adminId) {
        return adminRepository.findById(adminId).orElseThrow(
                () -> new ServiceException(ErrorCode.ADMIN_NOT_FOUND)
        );
    }

    // 관리자가 활성상태가 맞는지 확인하는 로직
    public void isActiveAdmin(Admin admin) {
        //isLoginable 은 활성(Active) 상태에서만 true 니까 활성상태가 아니라면 throw
        if (!admin.getStatus().isLoginable()) {
            switch (admin.getStatus()) {
                case PENDING -> throw new ServiceException(ErrorCode.ADMIN_PENDING);   // "계정 승인대기 중"
                case REJECTED -> throw new ServiceException(ErrorCode.ADMIN_REJECTED); // "계정 신청 거부됨"
                case STOPPED -> throw new ServiceException(ErrorCode.ADMIN_STOPPED);   // "계정 정지됨"
                case INACTIVE -> throw new ServiceException(ErrorCode.ADMIN_INACTIVE); // "계정 비활성화됨"
                default -> throw new ServiceException(ErrorCode.FORBIDDEN_ADMIN);
            }
        }
    }


    // 주문 취소 (관리자)
    @Transactional
    public CancelOrderResponse cancelByAdmin(Long orderId, Long sessionAdminId, CancelOrderRequest request) {

        // 관리자 활성 상태 확인
        isActiveAdmin(getAdminById(sessionAdminId));

        // 주문 확인
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ORDERING_NOT_FOUND));

        // 재고 다시 수량 올리기
        Product product = order.getProduct();
        product.updateStock(product.getStock()+order.getQuantity()); // restoreStock 변경 필요

        // 상태 변경 + 취소 사유 저장
        order.cancel(request.getCancelReason());

        return new CancelOrderResponse(
                order.getId(),
                order.getOrderNo(),
                order.getOrderStatus().getStatusName(),
                order.getCancelReason()
        );

    }
}
