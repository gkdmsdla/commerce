package com.example.commerce.admin.dto;

import com.example.commerce.admin.entity.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

public record RejectResponse (
        // 승인이 허가되지 않은 관리자의
        // id, 직책, 이유, 시간
        long rejectedAdminId,
        String role,
        String rejectReason,
        LocalDateTime rejectedAt
){}

//@Getter
//@RequiredArgsConstructor
//public class RejectResponse {
//    // 승인이 허가되지 않은 관리자의
//    // id, 직책, 이유, 시간
//    private final long rejectedAdminId;
//    //private final Role role;
//    private final String role;
//    private final String rejectReason;
//    private final LocalDateTime rejectedAt;
//}
