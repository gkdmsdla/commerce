package com.example.commerce.order.entity;

import com.example.commerce.admin.entity.Admin;
import com.example.commerce.customer.entity.Customer;
import com.example.commerce.global.common.BaseEntity;
import com.example.commerce.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //    주문 번호 -> orderNo
    @Column(nullable = false)
    private UUID orderNo = UUID.randomUUID();


    //    주문 상태 (이넘사용)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;


    //    주문 수량 -> ordercount
    @Column(nullable = false)
    private int quantity;


    private long totalPrice;

    // 상품 금액
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin; //cs 관리자가 주문을 생성했을 때 저장됨 -> nullable

    public Order(int quantity, long totalPrice, Product product, Customer customer, Admin admin) {
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.product = product;
        this.customer = customer;
        this.admin = admin;
    }
}