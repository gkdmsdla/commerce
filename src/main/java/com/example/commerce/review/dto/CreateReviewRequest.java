package com.example.commerce.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
public class CreateReviewRequest {
    @NotBlank(message = "리뷰 내용은 필수입니다.")
    private String content;

    @NotNull(message = "리뷰 평점은 필수입니다.")
    @Range(min = 1, max = 5, message = "1에서 5사이의 별점만 가능합니다.")
    private Integer rating;
}
