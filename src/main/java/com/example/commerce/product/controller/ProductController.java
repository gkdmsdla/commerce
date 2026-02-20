package com.example.commerce.product.controller;

import com.example.commerce.product.dto.*;
import com.example.commerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 생성
    @PostMapping
    public ResponseEntity<CreateProductResponse> create(@RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<GetOneProductResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getOne(id));
    }

    // 전체 조회
    @GetMapping
    ResponseEntity<List<GetOneProductResponse>> getAll(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(productService.getAll(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateProductResponse> update(@Valid Long id, @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }



}
