package com.example.commerce.order.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateAdminOrderResponse(

        UUID orderNo,
        String orderStatus,
        Long customersId,
        Long AdminId,
        String ProductId,
        LocalDateTime CreatedAt,
        LocalDateTime ModifiedAt
) {
}
