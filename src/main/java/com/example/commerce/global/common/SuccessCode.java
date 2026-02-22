package com.example.commerce.global.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {
    // ErrorCode 처럼 편히 만들어서 사용하십쇼!
    LOGIN_SUCCESSFUL(HttpStatus.OK, "로그인이 정상적으로 완료되었습니다."),
    CREATE_SUCCESSFUL(HttpStatus.CREATED, "데이터가 정상적으로 저장되었습니다."),
    CUSTOMER_SIGNUP(HttpStatus.OK, "고객 회원가입이 정상적으로 처리되었습니다."),
    ADMIN_SIGNUP(HttpStatus.CREATED, "관리자 신청이 완료되었습니다."),
    STATUS_PATCHED(HttpStatus.OK, "상태가 업데이트 되었습니다."),
    ORDER_SUCCESSFUL(HttpStatus.CREATED, "주문이 완료되었습니다."),
    DELETE_SUCCESSFUL(HttpStatus.OK, "삭제가 정상적으로 완료되었습니다."),
    GET_SUCCESSFUL(HttpStatus.OK, "조회가 완료되었습니다."),
    DATA_UPDATED(HttpStatus.OK, "정보가 업데이트 되었습니다.")
    ;

    private final HttpStatus status;
    private final String message;
}
