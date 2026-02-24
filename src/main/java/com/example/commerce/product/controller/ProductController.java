package com.example.commerce.product.controller;

import com.example.commerce.global.common.CommonResponseDTO;
import com.example.commerce.global.common.CommonResponseHandler;
import com.example.commerce.global.common.SuccessCode;
import com.example.commerce.global.security.UserPrincipal;
import com.example.commerce.product.dto.*;
import com.example.commerce.product.entity.Category;
import com.example.commerce.product.entity.ProductStatus;
import com.example.commerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 생성
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN')")
    @PostMapping("/admins/products")
    public ResponseEntity<CommonResponseDTO<CreateProductResponse>> create(
            @Valid @RequestBody CreateProductRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        // 상품은 관리자만 등록할 수 있음

        CreateProductResponse response = productService.create(request, userPrincipal);

        return CommonResponseHandler.success(SuccessCode.CREATE_SUCCESSFUL, response);
    }

    // 단건 조회
    @GetMapping("/products/{productId}")
    public ResponseEntity<CommonResponseDTO<GetOneProductResponse>> getOne(@PathVariable Long productId) {
        GetOneProductResponse response = productService.getOne(productId);
        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response);
    }

    // 전체 조회
    @GetMapping("/products")
    public ResponseEntity<CommonResponseDTO<List<GetProductsResponse>>> getProductsList(

            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Category category,
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

        Page<GetProductsResponse> response = productService.getAll(keyword, category, status, pageable);

        return CommonResponseHandler.success(SuccessCode.GET_SUCCESSFUL, response.getContent());
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @PutMapping("/products/{productId}")
    public ResponseEntity<CommonResponseDTO<UpdateProductResponse>> update(
            @PathVariable Long productId,@Valid @RequestBody UpdateProductRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UpdateProductResponse response = productService.update(productId, request, userPrincipal);
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED, response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<CommonResponseDTO<Void>> delete(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        productService.delete(productId, userPrincipal);
        return CommonResponseHandler.success(SuccessCode.DELETE_SUCCESSFUL);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @PatchMapping("/products/{productId}/discontinue")
    public ResponseEntity<CommonResponseDTO<String>> discontinueProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserPrincipal userPrincipal){
        productService.discontinue(productId,userPrincipal);
        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED, "상품이 단종 상태로 변경되었습니다.");
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OP_ADMIN', 'CS_ADMIN')")
    @PatchMapping("/products/{proudctId}")
    public ResponseEntity<CommonResponseDTO<UpdateStockResponse>> restockProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid UpdateStockRequest request){
        UpdateStockResponse response = productService.restock(productId, request, userPrincipal);

        return CommonResponseHandler.success(SuccessCode.DATA_UPDATED, response);
    }

}
