package com.example.commerce.product.entity;

import com.example.commerce.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 고유 id

    @Column(nullable = false, length = 50)
    private String name; // 상품 이름

    @Column(nullable = false, length = 100)
    private Category category; // 상품 설명

    @Column(nullable = false)
    private int price; // 상품 가격
    @Column(nullable = false)
    private int stock; // 재고 수량
    private ProductStatus status;

    // 상품 생성
    public Product (String name, Category category, int price, int stock, ProductStatus status){
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

    // 상품 수정
    public void update (String name, Category category, int price, int stock, ProductStatus status) {
        if (name != null) this.name = name;
        if (category != null) this.category = category;
        this.price = price;
        this.stock = stock;
        if (status != null) this.status = status;
    }

    // 변경-> update 함수명 변경 완
    // 상태 변경
    public void updateStatus() {
        if (this.stock <=0) {
            this.stock = 0; //마이너스 방지
            this.status = ProductStatus.SOLD_OUT;
        } else {
            this.status = ProductStatus.AVAILABLE;
        }
    }

    // 카테고리 변경
    public void updateCategory(Category category) {
        this.category = category;
    }

    // 재고처리
    public void updateStock(int stock){
        this.stock = stock;
        updateStatus();
    }

}
