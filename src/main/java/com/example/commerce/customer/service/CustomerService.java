package com.example.commerce.customer.service;

import com.example.commerce.admin.dto.AdminDetailResponse;
import com.example.commerce.admin.entity.Admin;
import com.example.commerce.admin.repository.AdminRepository;
import com.example.commerce.admin.service.AdminService;
import com.example.commerce.customer.dto.*;
import com.example.commerce.customer.entity.Customer;
import com.example.commerce.customer.entity.CustomerStatus;
import com.example.commerce.customer.repository.CustomerRepository;
import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//@RequiredArgsConstructor
public class CustomerService {

    // 고객 생성
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;

    public CustomerService(CustomerRepository customerRepository, AdminRepository adminRepository) {
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
    }

    @Transactional
    public SignupCustomerResponse createCustomerResponse(SignupCustomerRequest request) {

        // 이메일 중복 체크
        if (customerRepository.existsByEmail(request.getCustomerEmail())) {
            throw new ServiceException(ErrorCode.DUPLICATE_EMAIL);
        }

        // customer 생성
        Customer customer = new Customer(
                request.getCustomerName(),
                request.getCustomerEmail(),
                request.getCustomerPassword(),
                request.getCustomerPhone(),
                CustomerStatus.ACTIVE
        );

        // 저장
        Customer saved = customerRepository.save(customer);

        // 응답 DTO 변환
        return new SignupCustomerResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getPhone(),
                saved.getStatus().getStatusName(),
                saved.getCreatedAt()
        );

    }

    // 로그인

    @Transactional
    public LoginCustomerResponse customerLogin(LoginCustomerRequest request, HttpSession session) {

        // 이메일 확인
        Customer customer = customerRepository.findByEmail(request.getCustomerEmail())
                .orElseThrow(() -> new ServiceException(ErrorCode.WRONG_PW));

        // 비밀번호 확인
        if (!customer.getPassword().equals(request.getCustomerPassword())) {
            throw new ServiceException(ErrorCode.WRONG_PW);
        }

        // 로그인 성공 > 세션에 로그인 정보 저장
        session.setAttribute("LOGIN_CUSTOMER", customer.getId());

        return new LoginCustomerResponse(
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
    public Page<GetOneCustomerResponse> findAllCustomer(Long sessionAdminId, String keyword, CustomerStatus status, PageRequest pageable) {

        isActiveAdmin(getAdminById(sessionAdminId));

        Page<Customer> customers = customerRepository.searchCustomers(keyword, status, pageable);

        return customers.map(customer -> new GetOneCustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getStatus().getStatusName(),
                customer.getCreatedAt(),
                customer.getModifiedAt()
        ));
    }


//        return customerRepository.findAll()
//                .stream()
//                .map(customer -> new GetOneCustomerResponse(
//                        customer.getId(),
//                        customer.getName(),
//                        customer.getEmail(),
//                        customer.getPhone(),
//                        customer.getStatus().getStatusName(),
//                        customer.getCreatedAt(),
//                        customer.getModifiedAt()
//                ))
//                .toList();
//    }

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