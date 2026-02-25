package com.example.commerce.review.controller;

import com.example.commerce.global.common.CommonResponseDTO;
import com.example.commerce.global.common.CommonResponseHandler;
import com.example.commerce.global.common.SuccessCode;
import com.example.commerce.global.security.UserPrincipal;
import com.example.commerce.review.dto.CreateReviewRequest;
import com.example.commerce.review.dto.CreateReviewResponse;
import com.example.commerce.review.dto.GetOneReviewResponse;
import com.example.commerce.review.dto.GetReviewsResponse;
import com.example.commerce.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders/{orderId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    // 1. 리뷰 생성
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    ResponseEntity<CommonResponseDTO<CreateReviewResponse>> create (
            @PathVariable Long orderId,
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ){

        CreateReviewResponse response = reviewService.create(orderId, request, userPrincipal);

        return CommonResponseHandler.success(SuccessCode.CREATE_SUCCESSFUL, response);
    }

    // 2. 리뷰 단건 조회
    @GetMapping("/{reviewId}")
    ResponseEntity<CommonResponseDTO<GetOneReviewResponse>> getOne(
            @PathVariable Long orderId,
            @PathVariable Long reviewId){

        GetOneReviewResponse response = reviewService.getOneReview(orderId, reviewId);

        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response);
    }

    // 3. 리뷰 전체 조회
    @GetMapping
    ResponseEntity<CommonResponseDTO<List<GetReviewsResponse>>> getAll(
            @PathVariable Long orderId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer rating,

            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ){
        Page<GetReviewsResponse> response = reviewService.getReviews(orderId, keyword, rating, pageable);

        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response.getContent());
    }

    // 4. 리뷰 삭제
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN')")
    @DeleteMapping("/{reviewId}")
    ResponseEntity<CommonResponseDTO<Void>> delete(
            @PathVariable Long orderId,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ){
        reviewService.deleteReview(orderId, reviewId, userPrincipal);

        return CommonResponseHandler.success(SuccessCode.DELETE_SUCCESSFUL);
    }

}
