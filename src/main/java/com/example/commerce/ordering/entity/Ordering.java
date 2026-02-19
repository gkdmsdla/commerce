package com.example.commerce.ordering.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "Orders")
@NoArgsConstructor
public class Ordering {

    @Id
    @GeneratedValue
    private Long orderId; // 주문 아이디

    @Column
    private String orderNo = UUID.randomUUID().toString();

}
