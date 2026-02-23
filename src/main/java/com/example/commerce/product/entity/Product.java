package com.example.commerce.product.entity;

import com.example.commerce.global.common.BaseEntity;
import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
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
        this.name = name;
        this.category = category;

        if (priceIsValid(price)) this.price = price;
        if (stockIsValid(stock)) this.stock = stock;

        this.status = status;
    }

    public boolean priceIsValid(int price){
        if (price<0) throw new ServiceException(ErrorCode.MINUS_PRICE);
        return true;
    }

    public boolean stockIsValid(int stock){
        if (stock<0) throw new ServiceException(ErrorCode.SHORT_STOCK);
        return true;
    }

    // 재고처리
    public void updateStock(int stock){
        // 계산된 값이 들어오도록 함
        if(stockIsValid(stock)){
            this.stock = stock;
        }

        if (this.stock == 0 && status!=ProductStatus.DISCONTINUED) {
            updateStatus(ProductStatus.SOLD_OUT);
        }

//        this.stock = stock;
//        updateStatus();
    }

    // 변경-> update 함수명 변경 완
    // 상태 변경
    public void updateStatus(ProductStatus productStatus) {
        this.status = productStatus;
    }
}
