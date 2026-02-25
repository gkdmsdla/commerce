package com.example.commerce.product.service;

import com.example.commerce.admin.entity.Admin;
import com.example.commerce.admin.repository.AdminRepository;
import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
import com.example.commerce.global.security.UserPrincipal;
import com.example.commerce.product.dto.*;
import com.example.commerce.product.entity.Category;
import com.example.commerce.product.entity.Product;
import com.example.commerce.product.entity.ProductStatus;
import com.example.commerce.product.repository.ProductRepository;
import com.example.commerce.review.dto.GetOneReviewResponse;
import com.example.commerce.review.dto.ReviewRating;
import com.example.commerce.review.entity.Review;
import com.example.commerce.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final AdminRepository adminRepository;
    private final ReviewRepository reviewRepository;

    // 생성
    @Transactional
    public CreateProductResponse create(CreateProductRequest request, UserPrincipal userPrincipal){

        Admin admin = getAdminById(userPrincipal.getId());
        isActiveAdmin(admin);

        ProductStatus status = ProductStatus.from(request.getProductStatus());
        Category category = Category.from(request.getCategory());

        Product product = new Product(
                request.getProductName(),
                category,
                request.getProductPrice(),
                request.getProductStock(),
                status,
                admin
        );

        Product saved = productRepository.save(product);
        return new CreateProductResponse(
                saved.getId(),
                saved.getName(),
                saved.getCategory().getCategoryName(),
                saved.getPrice(),
                saved.getStock(),
                saved.getStatus().getStatusName(),
                saved.getCreatedAt(),
                saved.getAdmin().getName()
        );
    }

    // 단건 조회
    public GetOneProductResponse getOne (Long productId) {
        Product product = getProductById(productId);

        // 상품 개별 조회 때 리뷰 3개 +
        // 평균 평점, 전체 리뷰 개수, 별점별 개수 출력
        Pageable pageable = PageRequest.of(0, 3);

        List<Review> reviews = reviewRepository.searchReviewsByProductId(product.getId(), pageable);
        List<GetOneReviewResponse> dtos = new ArrayList<>();
        for (Review review : reviews) {
            GetOneReviewResponse dto = new GetOneReviewResponse(
                    review.getId(),
                    review.getOrder().getOrderNo(),
                    review.getOrder().getCustomer().getName(),
                    review.getOrder().getCustomer().getEmail(),
                    review.getOrder().getProduct().getName(),
                    review.getRating(),
                    review.getContent(),
                    review.getCreatedAt()
            );
            dtos.add(dto);
        }

        // 리뷰 개수
        Integer countOfReview = reviewRepository.countByProductId(product.getId());

        // [수정 완료] 리뷰 평균 (NPE 방지 로직 적용)
        Double avg = reviewRepository.averageRating(product.getId());


        double averageOfReview = (avg != null) ? Math.round(avg * 10) / 10.0 : 0.0;

        // 별점 별 리뷰 개수
        List<ReviewRating> reviewRatingList = reviewRepository.countListByProductId(product.getId());

        return new GetOneProductResponse(
                product.getName(),
                product.getCategory().getCategoryName(),
                product.getPrice(),
                product.getStock(),
                product.getStatus().getStatusName(),
                product.getCreatedAt(),
                product.getAdmin().getName(),
                product.getAdmin().getEmail(),

                dtos,
                countOfReview,
                averageOfReview,
                reviewRatingList
        );
    }

    // 전체 조회
    public Page<GetProductsResponse> getAll(String keyword, Category category, ProductStatus status, Pageable pageable) {
        Page<Product> products = productRepository.searchProducts(keyword, category, status, pageable);

        return products.map(product -> new GetProductsResponse(
                product.getId(),
                product.getName(),
                product.getCategory().getCategoryName(),
                product.getPrice(),
                product.getStock(),
                product.getStatus().getStatusName(),
                product.getCreatedAt(),
                product.getAdmin().getName()
        ));
    }

    // 수정
    @Transactional
    public UpdateProductResponse update(Long productId, UpdateProductRequest request , UserPrincipal userPrincipal){
        isActiveAdmin(getAdminById(userPrincipal.getId()));
        Product product = getProductById(productId);

        Category category = Category.from(request.getCategory());

        product.update(
                request.getProductName(),
                category,
                request.getProductPrice()
        );

        return new UpdateProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory().getCategoryName(),
                product.getPrice(),
                product.getStatus().getStatusName(),
                product.getModifiedAt());
    }

    @Transactional
    public UpdateStockResponse restock(Long productId, UpdateStockRequest request, UserPrincipal userPrincipal) {
        isActiveAdmin(getAdminById(userPrincipal.getId()));

        Product product = getProductById(productId);
        int previousStock = product.getStock();

        product.updateStock(request.getStock());

        return new UpdateStockResponse(
                product.getId(),
                product.getName(),
                previousStock,
                product.getStock(),
                product.getModifiedAt()
        );
    }

    // 삭제
    @Transactional
    public void delete(Long productId, UserPrincipal userPrincipal) {
        isActiveAdmin(getAdminById(userPrincipal.getId()));
        Product product = getProductById(productId);

        productRepository.delete(product);
    }

    @Transactional
    public void discontinue(Long productId, UserPrincipal userPrincipal) {
        isActiveAdmin(getAdminById(userPrincipal.getId()));

        Product product = getProductById(productId);

        if (product.getStatus() == ProductStatus.DISCONTINUED){
            throw new ServiceException(ErrorCode.UNABLE_TO_WORK_STATUS);
        }

        product.updateStatus(ProductStatus.DISCONTINUED);
    }

    public Product getProductById(Long productId){
        return productRepository.findById(productId).orElseThrow(
                ()-> new ServiceException(ErrorCode.PRODUCT_NOT_FOUND));
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
                case STOPPED -> throw new ServiceException(ErrorCode.ACCOUNT_STOPPED);   // "계정 정지됨"
                case INACTIVE -> throw new ServiceException(ErrorCode.ACCOUNT_INACTIVE); // "계정 비활성화됨"
                default -> throw new ServiceException(ErrorCode.FORBIDDEN_ADMIN);
            }
        }
    }
}