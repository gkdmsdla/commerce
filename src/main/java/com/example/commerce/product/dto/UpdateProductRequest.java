package com.example.commerce.product.dto;

import com.example.commerce.product.entity.ProductCategory;
import com.example.commerce.product.entity.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateProductRequest {
    @NotBlank(message = "상품명을 입력해주세요.")
    private String productName;

    @NotBlank(message = "카테고리를 설정해주세요")
    private ProductCategory productCategory;

    @NotNull
    private int productPrice;
    @NotNull
    private int productStock;

    private ProductStatus productStatus;
}
