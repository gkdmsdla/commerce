package com.example.commerce.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // user 관련 에러
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "E001", "중복된 이메일 입니다."),

    WRONG_PW(HttpStatus.UNAUTHORIZED, "E002", "비밀번호가 일치하지 않습니다."),
    BEFORE_LOGIN(HttpStatus.UNAUTHORIZED, "E003", "로그인이 필요합니다."),

    FORBIDDEN_ADMIN(HttpStatus.FORBIDDEN, "E004", "권한이 없습니다."),
    CUSTOMER_MISMATCH(HttpStatus.FORBIDDEN, "E005", "주문자 본인만 접근할 수 있습니다."),

    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "E006", "존재하지 않는 관리자입니다."),
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "E007", "존재하지 않는 고객입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "E008", "존재하지 않는 상품입니다."),
    ORDERING_NOT_FOUND(HttpStatus.NOT_FOUND, "E009", "존재하지 않는 주문입니다."),

    CANCEL_FORBIDDEN(HttpStatus.BAD_REQUEST, "E010", "주문 취소 불가 상태입니다."),
    SHORT_STOCK(HttpStatus.BAD_REQUEST, "E011", "재고가 부족합니다."),
    DISCONTINUED(HttpStatus.BAD_REQUEST, "E012", "단종된 상품입니다."),

    // 계정 관련 오류 코드 추가
    ADMIN_PENDING(HttpStatus.FORBIDDEN, "E013", "계정 승인이 대기 중입니다."),
    ADMIN_REJECTED(HttpStatus.FORBIDDEN, "E014", "계정 신청이 거부되었습니다."),
    ADMIN_STOPPED(HttpStatus.FORBIDDEN, "E015", "정지된 계정입니다."),
    ADMIN_INACTIVE(HttpStatus.FORBIDDEN, "E016", "비활성화된 계정입니다."),
    INVALID_STATUS(HttpStatus.BAD_REQUEST, "E017", "해당 작업을 수행할 수 없는 상태입니다."),

    INVALID_ROLE(HttpStatus.BAD_REQUEST, "E018", "유효하지 않은 직책입니다."),

    //공통에러는 C 로 시작
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다,"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 내부 오류가 발생했습니다.");

    // 상태코드, 내가 지정한 오류코드, 출력할 메시지
    private final HttpStatus status;
    private final String code;
    private final String message;
}
