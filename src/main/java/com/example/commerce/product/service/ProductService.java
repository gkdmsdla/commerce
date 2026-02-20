package com.example.commerce.product.service;

import com.example.commerce.product.dto.*;
import com.example.commerce.product.entity.Product;
import com.example.commerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    // 생성
    @Transactional
    public CreateProductResponse create(CreateProductRequest request){
        Product product = new Product(
                request.getProductName(),
                request.getProductCategory(),
                request.getProductPrice(),
                request.getProductStock(),
                request.getProductStatus()
        );
        Product saved = productRepository.save(product);
        return new CreateProductResponse(
                saved.getId(),
                saved.getName(),
                saved.getCategory(),
                saved.getPrice(),
                saved.getStock(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public GetOneProductResponse getOne (Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 상품입니다."));

        return new GetOneProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getStock(),
                product.getStatus(),
                product.getCreatedAt());
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public List<GetOneProductResponse> getAll(String name) {
        List<Product> products = (name != null)
                ? productRepository.findAllByProductnameOrderByCreatedAtDesc(name)
                : productRepository.findAllByOrderByCreatedAtDesc();

        return products.stream()
                .map(product -> new GetOneProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getCategory(),
                        product.getPrice(),
                        product.getStock(),
                        product.getStatus(),
                        product.getCreatedAt())
                ).toList();
    }

    // 수정
    @Transactional
    public UpdateProductResponse update(Long id, UpdateProductRequest request){
        Product product = productRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("존재 하지 않는 상품입니다."));

        product.update(
                request.getProductName(),
                request.getProductCategory(),
                request.getProductPrice(),
                request.getProductStock(),
                request.getProductStatus()
        );
        return new UpdateProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getStock(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getModifiedAt());
    }

    // 삭제
    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("존재 하지 않는 상품입니다."));
        productRepository.delete(product);
    }



}
