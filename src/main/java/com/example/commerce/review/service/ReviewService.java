package com.example.commerce.review.service;

import com.example.commerce.admin.entity.Admin;
import com.example.commerce.admin.repository.AdminRepository;
import com.example.commerce.customer.entity.Customer;
import com.example.commerce.customer.entity.CustomerStatus;
import com.example.commerce.customer.repository.CustomerRepository;
import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
import com.example.commerce.global.security.UserPrincipal;
import com.example.commerce.order.entity.Order;
import com.example.commerce.order.entity.OrderStatus;
import com.example.commerce.order.repository.OrderRepository;
import com.example.commerce.product.repository.ProductRepository;
import com.example.commerce.review.dto.CreateReviewRequest;
import com.example.commerce.review.dto.CreateReviewResponse;
import com.example.commerce.review.dto.GetOneReviewResponse;
import com.example.commerce.review.dto.GetReviewsResponse;
import com.example.commerce.review.entity.Review;
import com.example.commerce.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final AdminRepository adminRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;

    //1. 리뷰 생성
    @Transactional
    public CreateReviewResponse create(Long orderId, CreateReviewRequest request, UserPrincipal userPrincipal) {
        Customer customer = getCustomerById(userPrincipal.getId());
        // 고객이 활성상태인지
        isActiveCustomer(customer);

        Order order = getOrderById(orderId);
        // 고객이 정말 이 제품을 주문했는지
        if (order.getCustomer().getId()!= customer.getId()){
            throw new ServiceException(ErrorCode.FORBIDDEN_CUSTOMER);
        }
        // 이미 리뷰를 작성했거나 배달 완료 상태가 아닐때 ( 수정사항 : == 를 != 로 변경)
        if (order.isReviewed() || order.getOrderStatus() != OrderStatus.DELIVERED){
            throw new ServiceException(ErrorCode.INVALID_STATUS);
        }

        // 리뷰 생성
        Review review = new Review(
                request.getContent(), // 내용
                request.getRating(), // 별점
                order // 해당 주문
        );

        // 저장
        Review savedReview = reviewRepository.save(review);
        order.updateIsReviewd();

        return new CreateReviewResponse(
                savedReview.getId(),
                savedReview.getOrder().getProduct().getName(), // 주문한 상품명
                savedReview.getOrder().getCustomer().getName(), // 주문한 고객명
                savedReview.getOrder().getCreatedAt(), // 주문시각
                savedReview.getRating(),
                savedReview.getContent()
        );
    }

    // 2. 리뷰 단건 조회
    public GetOneReviewResponse getOneReview(Long orderId, Long reviewId) {
        Order order = getOrderById(orderId);

        Review review = getReviewById(reviewId);

        if (review.getOrder().getId() != order.getId()){
            throw new ServiceException(ErrorCode.REVIEW_NOT_FOUND);
        }

        return new GetOneReviewResponse(
                review.getId(),
                review.getOrder().getOrderNo(),
                review.getOrder().getCustomer().getName(),
                review.getOrder().getCustomer().getEmail(),
                review.getOrder().getProduct().getName(),
                review.getRating(),
                review.getContent(),
                review.getCreatedAt()
        );
    }

    // 3. 리뷰 전체조회
    public Page<GetReviewsResponse> getReviews(Long orderId, String keyword, Integer rating, Pageable pageable) {
        Order order = getOrderById(orderId);

        // 특정 상품에 달린 리뷰만 get
        Page<Review> reviews = reviewRepository.searchReviews(order.getProduct().getId(), keyword, rating, pageable);

        return reviews.map(review -> new GetReviewsResponse(
                review.getId(),
                review.getOrder().getOrderNo(),
                review.getOrder().getCustomer().getName(),
                review.getOrder().getProduct().getName(),
                review.getRating(),
                review.getContent(),
                review.getCreatedAt()
        ));
    }

    @Transactional
    public void deleteReview(Long orderId, Long reviewId, UserPrincipal userPrincipal) {
        Order order = getOrderById(orderId);

        isActiveAdmin(getAdminById(userPrincipal.getId()));

        Review review = getReviewById(reviewId);

        // 여기 수정
        if (review.getOrder().getId() != order.getId()){
            throw new ServiceException(ErrorCode.REVIEW_NOT_FOUND);
        }

        reviewRepository.deleteById(review.getId());
    }

    // -----------------Controller 에서 사용하는 메서드 아닌 애들 ------------------------

    public Review getReviewById(Long reviewId){
        return reviewRepository.findById(reviewId).orElseThrow(
                ()-> new ServiceException(ErrorCode.REVIEW_NOT_FOUND)
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
                case STOPPED -> throw new ServiceException(ErrorCode.ACCOUNT_STOPPED);   // "계정 정지됨"
                case INACTIVE -> throw new ServiceException(ErrorCode.ACCOUNT_INACTIVE); // "계정 비활성화됨"
                default -> throw new ServiceException(ErrorCode.FORBIDDEN_ADMIN);
            }
        }
    }

    public Order getOrderById(Long orderId){
        return orderRepository.findById(orderId).orElseThrow(
                () -> new ServiceException(ErrorCode.ORDERING_NOT_FOUND));
    }

    public Customer getCustomerById(Long customerId){
        return customerRepository.findById(customerId).orElseThrow(
                ()-> new ServiceException(ErrorCode.CUSTOMER_NOT_FOUND));
    }

    //Customer 상태가 ACTIVE 가 아니라면 throw
    public void isActiveCustomer(Customer customer){
        if (customer.getStatus()== CustomerStatus.INACTIVE){
            throw new ServiceException(ErrorCode.ACCOUNT_INACTIVE);
        }
        else if (customer.getStatus()==CustomerStatus.SUSPENDED){
            throw new ServiceException(ErrorCode.ACCOUNT_STOPPED);
        }
    }
}
