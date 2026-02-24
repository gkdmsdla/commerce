package com.example.commerce.customer.service;

import com.example.commerce.customer.dto.*;
import com.example.commerce.customer.entity.Customer;
import com.example.commerce.customer.entity.CustomerStatus;
import com.example.commerce.customer.repository.CustomerRepository;
import com.example.commerce.global.config.PasswordEncoder;
import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
import com.example.commerce.global.security.JwtUtil;
import com.example.commerce.global.security.entity.RefreshToken;
import com.example.commerce.global.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    // [추가] JWT 발급을 위한 JwtUtil 의존성 주입
    private final JwtUtil jwtUtil;

    @Transactional
    public CreateCustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByEmail(request.getCustomerEmail())) {
            throw new ServiceException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.getCustomerPassword());

        Customer customer = new Customer(
                request.getCustomerName(),
                request.getCustomerEmail(),
                encodedPassword,
                request.getCustomerPhone(),
                CustomerStatus.ACTIVE //  고객은 기본 활성 상태
        );

        Customer savedCustomer = customerRepository.save(customer);

        return new CreateCustomerResponse(
                savedCustomer.getId(),
                savedCustomer.getName(),
                savedCustomer.getEmail(),
                savedCustomer.getPhone(),
                savedCustomer.getStatus().getStatusName(),
                savedCustomer.getCreatedAt(),
                savedCustomer.getModifiedAt()
        );
    }

    // 로그인(JWT 토큰 활용)
    @Transactional(readOnly = true)
    public LoginCustomerResponse customerLogIn(LoginCustomerRequest request) {
        Customer customer = customerRepository.findByEmail(request.getCustomerEmail())
                .orElseThrow(() -> new ServiceException(ErrorCode.CUSTOMER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getCustomerPassword(), customer.getPassword())) {
            throw new ServiceException(ErrorCode.WRONG_PW);
        }

        // 활성 상태 검증 (정지, 비활성 고객은 로그인 불가)
        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new ServiceException(ErrorCode.INVALID_STATUS);
        }

        // JWT 토큰 생성 (역할은 "CUSTOMER"로 명시)
        //String token = jwtUtil.createToken(customer.getId(), customer.getEmail(), "CUSTOMER");
        String accessToken = jwtUtil.createToken(customer.getId(), customer.getEmail(), customer.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(customer.getEmail());

        // 2. Refresh Token DB 저장 (이미 존재하면 Update, 없으면 Insert)
        RefreshToken tokenEntity = refreshTokenRepository.findByEmail(customer.getEmail())
                .orElse(new RefreshToken(customer.getEmail(), refreshToken));

        tokenEntity.updateToken(refreshToken);
        refreshTokenRepository.save(tokenEntity);

        return new LoginCustomerResponse(
                accessToken,
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getStatus().getStatusName()
        );
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