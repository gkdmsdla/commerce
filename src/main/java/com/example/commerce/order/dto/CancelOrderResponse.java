package com.example.commerce.order.dto;

import java.util.UUID;

public record CancelOrderResponse(

        Long orderId,
        UUID orderNo,

        String orderStatus,
        String cancelReason
) { }
