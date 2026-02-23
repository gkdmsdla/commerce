package com.example.commerce.order.entity;

import com.example.commerce.admin.entity.Admin;
import com.example.commerce.customer.entity.Customer;
import com.example.commerce.global.common.BaseEntity;
import com.example.commerce.product.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.example.commerce.global.exception.ErrorCode;
import com.example.commerce.global.exception.ServiceException;
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

    // 주문 번호 -> orderNo
    @Column(nullable = false)
    private UUID orderNo = UUID.randomUUID();


    //    주문 상태 (이넘사용)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus statusName;


    //    주문 수량 -> ordercount
    @Column(nullable = false)
    private int quantity;




    // 주문 취소 사유
    @Column(nullable = false)
    private String cancelReason;



    // 주문 총 금액
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


    // 주문을 취소 상태로 변경
    // 엔티티에서 관리하는 이유 : 규칙이 한 곳에 있어서 수정이 편하다.
    public void cancel(String reason) {

        // 준비 상태일 때만 취소 가능
        // statusName이 PREPARING과 같지 않을 경우 true
        if (!this.statusName.equals(OrderStatus.PREPARING.toString()))  {
            throw new ServiceException(ErrorCode.CANCEL_FORBIDDEN);
        }
        // 취소로 상태 변경
        this.statusName = OrderStatus.CANCELED.name();
        // 취소 사유 저장
        this.cancelReason = reason;
    }
}