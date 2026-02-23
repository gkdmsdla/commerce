package com.example.commerce.product.controller;

import com.example.commerce.global.common.CommonResponseDTO;
import com.example.commerce.global.common.CommonResponseHandler;
import com.example.commerce.global.common.SuccessCode;
import com.example.commerce.product.dto.*;
import com.example.commerce.product.entity.ProductStatus;
import com.example.commerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 생성
    @PostMapping("/admins/products")
    public ResponseEntity<CommonResponseDTO<CreateProductResponse>> create(@Valid @RequestBody CreateProductRequest request) {
        // 상품은 관리자만 등록할 수 있음

        CreateProductResponse response = productService.create(request);

        return CommonResponseHandler.success(SuccessCode.CREATE_SUCCESSFUL, response);
    }

    // 단건 조회
    @GetMapping("/products/{productId}")
    public ResponseEntity<CommonResponseDTO<GetOneProductResponse>> getOne(@PathVariable Long productId) {
        GetOneProductResponse response = productService.getOne(productId);
        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response);
    }

    // 전체 조회
    @GetMapping("/admins")
    public ResponseEntity<CommonResponseDTO<List<GetAllProductResponse>>> getProductsList(

            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ProductStatus status,

            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,

            @RequestParam(required = false) String sort,
            @RequestParam(required = false) boolean desc

            ) {
        Sort.Direction direction = (desc) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortValue = "email";
        if ("price".equals(sort)) sortValue = "price";
        else if ("createdAt".equals(sort)) sortValue = "createdAt";

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortValue));

        Page<GetAllProductResponse> response = productService.getAll(keyword, status, pageable);

        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response.getContent());
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<CommonResponseDTO<UpdateProductResponse>> update(@PathVariable Long productId,@Valid @RequestBody UpdateProductRequest request) {
        UpdateProductResponse response = productService.update(productId, request);
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED, response);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<CommonResponseDTO<Void>> delete(@PathVariable Long productId) {
        productService.delete(productId);
        return CommonResponseHandler.success(SuccessCode.DELETE_SUCCESSFUL);
    }

    @PatchMapping("/products/{productId}")
    public ResponseEntity<CommonResponseDTO<String>> discontinueProduct(@PathVariable Long productId){
        productService.discontinue(productId);
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED, "상품이 단종 상태로 변경되었습니다.");
    }


}
