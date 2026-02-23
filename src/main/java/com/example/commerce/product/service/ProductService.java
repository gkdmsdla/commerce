package com.example.commerce.product.service;

import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
import com.example.commerce.product.dto.*;
import com.example.commerce.product.entity.Product;
import com.example.commerce.product.entity.ProductStatus;
import com.example.commerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    // 생성
    @Transactional
    public CreateProductResponse create(CreateProductRequest request){
        Product product = new Product(
                request.getProductName(),
                request.getCategory(),
                request.getProductPrice(),
                request.getProductStock(),
                request.getProductStatus()
        );
        Product saved = productRepository.save(product);
        return new CreateProductResponse(
                saved.getId(),
                saved.getName(),
                saved.getCategory().getCategoryName(),
                saved.getPrice(),
                saved.getStock(),
                saved.getStatus().getStatusName(),
                saved.getCreatedAt()
        );
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public GetOneProductResponse getOne (Long id) {
        Product product = productRepository.findById(id).orElseThrow(()
                -> new ServiceException(ErrorCode.PRODUCT_NOT_FOUND));

        return new GetOneProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory().getCategoryName(),
                product.getPrice(),
                product.getStock(),
                product.getStatus().getStatusName(),
                product.getCreatedAt());
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public Page<GetAllProductResponse> getAll(String keyword, ProductStatus status, PageRequest pageable) {
        //admin id 로 admin 을 찾고, 활성상태인지 확인
        //isActiveAdmin(getAdminById(sessionAdminId));
        Page<Product> products = productRepository.searchProducts(keyword, status, pageable);

//        List<Product> products = (keyword != null)
//                ? productRepository.findAllByProductnameOrderByCreatedAtDesc(keyword)
//                : productRepository.findAllByOrderByCreatedAtDesc();

        return products.map(product -> new GetAllProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getCategory().getCategoryName(),
                        product.getPrice(),
                        product.getStock(),
                        product.getStatus().getStatusName(),
                        product.getCreatedAt()));
    }

    // 수정
    @Transactional
    public UpdateProductResponse update(Long id, UpdateProductRequest request){
        Product product = productRepository.findById(id).orElseThrow(()
                -> new ServiceException(ErrorCode.PRODUCT_NOT_FOUND));

        product.update(
                request.getProductName(),
                request.getCategory(),
                request.getProductPrice(),
                request.getProductStock(),
                request.getProductStatus()
        );
        return new UpdateProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory().getCategoryName(),
                product.getPrice(),
                product.getStock(),
                product.getStatus().getStatusName(),
                product.getCreatedAt(),
                product.getModifiedAt());
    }

    // 삭제
    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id).orElseThrow(()
                -> new ServiceException(ErrorCode.PRODUCT_NOT_FOUND));
        productRepository.delete(product);
    }



}
