package com.example.commerce.customer.service;

import com.example.commerce.customer.dto.*;
import com.example.commerce.customer.entity.Customer;
import com.example.commerce.customer.repository.CustomerRepository;
import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    // 고객 생성
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CreateCustomerResponse createCustomerResponse(CreateCustomerRequest request) {

        // 이메일 중복 체크
        if (customerRepository.existsByEmail(request.getCustomerEmail())) {
            throw new ServiceException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 엔티티 생성 (status null = 기본값 ACTIVE)
        Customer customer = new Customer(
                request.getCustomerName(),
                request.getCustomerEmail(),
                request.getCustomerPassword(),
                request.getCustomerPhone(),
                request.getCustomerStatus()
        );

        // 저장
        Customer saved = customerRepository.save(customer);

        // 응답 DTO 변환
        return new CreateCustomerResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getPhone(),
                saved.getStatus(),
                saved.getStatus().getStatusName(),
                saved.getCreatedAt(),
                saved.getModifiedAt()
        );

    }

    // 로그인

    @Transactional
    public String customerLogin(LoginCustomerRequest request, HttpSession session) {

        // 이메일 확인
        Customer customer = customerRepository.findByEmail(request.getCustomerEmail())
                .orElseThrow(() -> new ServiceException(ErrorCode.WRONG_PW));

        // 비밀번호 확인
        if (!customer.getPassword().equals(request.getCustomerPassword())) {
            throw new ServiceException(ErrorCode.WRONG_PW);
        }

        // 로그인 성공 > 세션에 로그인 정보 저장
        session.setAttribute("LOGIN_CUSTOMER", customer.getId());

        return "로그인 성공";
    }

    // 고객 상세 조회
    @Transactional(readOnly = true)
    public GetOneCustomerResponse findCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ServiceException(ErrorCode.CUSTOMER_NOT_FOUND));

        return new GetOneCustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getStatus(),
                customer.getStatus().getStatusName(),
                customer.getCreatedAt(),
                customer.getModifiedAt()
        );
    }

    // 고객 리스트 조회
    @Transactional(readOnly = true)
    public List<GetOneCustomerResponse> findAllCustomer() {

        return customerRepository.findAll()
                .stream()
                .map(customer -> new GetOneCustomerResponse(
                        customer.getId(),
                        customer.getName(),
                        customer.getEmail(),
                        customer.getPhone(),
                        customer.getStatus(),
                        customer.getStatus().getStatusName(),
                        customer.getCreatedAt(),
                        customer.getModifiedAt()
                ))
                .toList();
    }

    // 유저 수정
    @Transactional
    public GetOneCustomerResponse updateCustomer(Long id, UpdateCustomerRequest request){
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ServiceException(ErrorCode.CUSTOMER_NOT_FOUND));

        // 이메일 중복 검사
        if (request.getCustomerEmail() != null){
            customerRepository.findByEmail(request.getCustomerEmail())
                    .filter(found -> !found.getId().equals(id))
                    .ifPresent(found -> {
                        throw new ServiceException(ErrorCode.DUPLICATE_EMAIL);
                    });
        }

        customer.update(
                request.getCustomerName(),
                request.getCustomerEmail(),
                request.getCustomerPhone()
        );

        return new GetOneCustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getStatus(),
                customer.getStatus().getStatusName(),
                customer.getCreatedAt(),
                customer.getModifiedAt()
        );
    }

    // 유저 삭제
    @Transactional
    public void deleteCustomer(Long id){

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ServiceException(ErrorCode.CUSTOMER_NOT_FOUND));

        customerRepository.delete(customer);

    }

}