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


    @Transactional
    public CreateOrderResponse create(long sessionCustomerId, @Valid CreateOrderRequest request) {
        // 세션에 저장되어있는 id 를 기반으로
        // customer repostiory 에서 customer 를 찾음
        Customer customer = customerRepository.findCustomerById(sessionCustomerId); //orElseThrow

        // 상품이 정말로 존재하는지
        Product product = productRepository.findProductById(request.getProductId()); //orElseThrow

        // 재고가 남아있는지 확인
        if (request.getQuantity()>product.getQuantity()){
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

    public CreateAdminOrderResponse createByAdmin(Long sessionAdminId, CreateAdminOrderRequest request) {
        // 관리자가 존재하는지
        Admin admin = adminRepository.findById(sessionAdminId).orElseThrow(
                ()-> new ServiceException(ErrorCode.ADMIN_NOT_FOUND)
        );

        // 관리자가 주문 관련 자격이 있는지 ...
        // 추가 필요

        // 요청한 고객이 존재하는지
        Customer customer = customerRepository.findCustomerById(request.getCustomerId()); //orElseThrow

        // 상품이 정말로 존재하는지
        Product product = productRepository.findProductById(request.getProductId()); //orElseThrow

        // 재고가 남아있는지 확인
        if (request.getQuantity()>product.getQuantity()){
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
                    order.getStatusName(),
                    order.getQuantity(),
                    order.getCreatedAt(),
                    order.getAdmin().getName()
            );
            dtos.add(dto);
        }
        return new PageImpl<>(dtos, pageable, orders.getTotalElements());
    }


    public Page<GetAllCustomerOrderResponse> getAllByCustomer(Pageable pageable){
        Page<Order> orders = orderRepository.findOrders(pageable);

        return orders.map(order -> new GetAllCustomerOrderResponse(
                order.getOrderNo(),
                order.getCustomer().getName(),
                order.getProduct().getName(),
                order.getStatusName()
        ));
    }


    // 수정을 수정 중입니다:p
    public UpdateAdminOrderResponse UAOR(UpdateAdminOrderRequest request){
        Admin admin = adminRepository.findById(sessionAdminId).orElseThrow(
                ()-> new ServiceException(ErrorCode.ADMIN_NOT_FOUND)
        );

        Customer customer = customerRepository.findCustomerById(request.getCustomerId()); //orElseThrow

        Product product = productRepository.findProductById(request.getProductId());

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

}
