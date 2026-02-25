package com.example.commerce.review.repository;

import com.example.commerce.review.dto.ReviewRating;
import com.example.commerce.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE " +
            "(:productId = r.order.product.id) AND " +
            "(:keyword IS NULL OR r.order.customer.name LIKE %:keyword% OR r.order.product.name LIKE %:keyword%) AND " +
            "(:rating IS NULL OR r.rating = :rating)")
    Page<Review> searchReviews(
            @Param("productId") Long productId,
            @Param("keyword") String keyword,
            @Param("rating") Integer rating,
            Pageable pageable);

    @Query("SELECT r FROM Review r WHERE " +
            "(:productId = r.order.product.id)")
    List<Review> searchReviewsByProductId(
            @Param("productId") Long productId,
            Pageable pageable);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.order.product.id = :productId")
    Integer countByProductId(@Param("productId") Long productId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.order.product.id = :productId")
    Double averageRating(@Param("productId") Long productId);

    @Query("SELECT new com.example.commerce.review.dto.ReviewRating(r.rating, COUNT(r)) FROM Review r " +
            "WHERE :productId = r.order.product.id " +
            "GROUP BY r.rating " +
            "ORDER BY r.rating")
    List<ReviewRating> countListByProductId(@Param("productId") Long productId);

    // 대시보드 전용 쿼리 추가

    /**
     * 1. 시스템 전체 리뷰 별점 분포 (Charts용)
     * 상품 ID 제한 없이 전체 리뷰를 대상으로 1~5점 개수를 집계합니다.
     */
    @Query("SELECT new com.example.commerce.review.dto.ReviewRating(r.rating, COUNT(r)) " +
            "FROM Review r GROUP BY r.rating ORDER BY r.rating")
    List<ReviewRating> countAllReviewRatings();

    // [추가] 대시보드 Summary 전용 전체 리뷰 수 및 평균 평점
    @Query("SELECT COUNT(r) FROM Review r")
    long countAllReviews();

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r")
    Double averageGlobalRating();
}