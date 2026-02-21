package com.example.commerce.order.service;

import com.example.commerce.admin.entity.Admin;
import com.example.commerce.admin.repository.AdminRepository;
import com.example.commerce.customer.entity.Customer;
import com.example.commerce.customer.repository.CustomerRepository;
import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.order.dto.*;
import com.example.commerce.order.entity.Order;
import com.example.commerce.order.entity.OrderStatus;
import com.example.commerce.product.entity.Product;
import com.example.commerce.global.exception.ServiceException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // customer repostiory 에서 customer 를 찾음
        Customer customer = customerRepository.findCustomerById(sessionCustomerId); //orElseThrow

        // 상품이 정말로 존재하는지
        Product product = productRepository.findProductById(request.getProductId()); //orElseThrow

        // 재고가 남아있는지 확인
        if (request.getQuantity() > product.getQuantity()) {
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

        return new CreateOrderResponse(
                newOrder.getId(),
                newOrder.getOrderNo(),
                newOrder.getProduct().getName(),
                newOrder.getProduct().getPrice(),
                newOrder.getQuantity(),
                newOrder.getTotalPrice(),
                OrderStatus.PREPARING,
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
        Customer customer = customerRepository.findCustomerById(request.getCustomerId()); //orElseThrow

        // 상품이 정말로 존재하는지
        Product product = productRepository.findProductById(request.getProductId()); //orElseThrow

        // 재고가 남아있는지 확인
        if (request.getQuantity() > product.getQuantity()) {
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

        return new CreateAdminOrderResponse(
                newOrder.getId(),
                newOrder.getOrderNo(),
                newOrder.getCustomer().getId(),
                newOrder.getCustomer().getName(),
                newOrder.getProduct().getName(),
                newOrder.getProduct().getPrice(),
                newOrder.getQuantity(),
                newOrder.getTotalPrice(),
                OrderStatus.PREPARING,
                newOrder.getCreatedAt(),
                newOrder.getAdmin().getName(),
                newOrder.getAdmin().getEmail(),
                newOrder.getAdmin().getRole()
        );
    }

    public long calculateTotalPrice(int quantity, int price) {
        return (long) quantity * price;
    }

    // 주문 단 건 조회 (관리자용)
    public GetOneAdminOrderResponse getOneAdminOrder(Long orderId, Long sessionAdminId) {

        isActiveAdmin(getAdminById(sessionAdminId));

        Order order = orderRepository.findById(orderId)
                .orElseTrow(() -> new ServiceException(ErrorCode.ORDERING_NOT_FOUND));


        //Order newOrder = orderRepository.save(order);

        return new GetOneAdminOrderResponse(
                order.getOrderNo(),
                order.getQuantity(),
                OrderStatus.PREPARING,
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
        Order order = orderRepository.findById(orderId)
                .orElseTrow(() -> new ServiceException(ErrorCode.ORDERING_NOT_FOUND));

        // 고객 로그인 체크
        if (sessionCustomerId == null) {
            throw new ServiceException(ErrorCode.CUSTOMER_MISMATCH);
        }

        // Order newOrder = orderRepository.save(order);

        return new GetOneOrderResponse(
                order.getOrderNo(),
                order.getQuantity(),
                OrderStatus.PREPARING,
                order.getCustomer().getName(),
                order.getCustomer().getEmail(),
                order.getProduct().getName(),
                order.getProduct().getPrice(),
                order.getCreatedAt()
        );
    }
    public Admin getAdminById(long adminId){
        return adminRepository.findById(adminId).orElseThrow(
                ()->new ServiceException(ErrorCode.ADMIN_NOT_FOUND)
        );
    }

    // 관리자가 활성상태가 맞는지 확인하는 로직
    public void isActiveAdmin(Admin admin){
        //isLoginable 은 활성(Active) 상태에서만 true 니까 활성상태가 아니라면 throw
        if(!admin.getStatus().isLoginable()){
            switch (admin.getStatus()) {
                case PENDING -> throw new ServiceException(ErrorCode.ADMIN_PENDING);   // "계정 승인대기 중"
                case REJECTED -> throw new ServiceException(ErrorCode.ADMIN_REJECTED); // "계정 신청 거부됨"
                case STOPPED -> throw new ServiceException(ErrorCode.ADMIN_STOPPED);   // "계정 정지됨"
                case INACTIVE -> throw new ServiceException(ErrorCode.ADMIN_INACTIVE); // "계정 비활성화됨"
                default -> throw new ServiceException(ErrorCode.FORBIDDEN_ADMIN);
            }
        }
    }

}