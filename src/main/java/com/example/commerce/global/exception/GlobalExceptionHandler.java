package com.example.commerce.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice // 모든 컨트롤러의 예외를 감시하는 어노테이션
public class GlobalExceptionHandler {

    /**
     * 1. 비즈니스 로직 예외 처리 (우리가 예상한 에러)
     * MemberService 등에서 던진 MemberException을 잡습니다.
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleMemberException(ServiceException e, HttpServletRequest request) {
        log.warn("MemberException : {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus()) //ERROR CODE 에 정의된 Http 상태코드
                .body(buildErrorResponse(errorCode, e.getMessage(), request.getRequestURI()));
    }

    /**
     * 2. 유효성 검사 실패 처리 (@Valid)
     * DTO의 @NotBlank, @Pattern 조건 등을 만족하지 못했을 때 발생합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("ValidationException : {}", e.getMessage());

        // 여러 에러 중 첫 번째 에러 메시지만 가져와서 보여줍니다.
        String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();

        return ResponseEntity
                .badRequest()
                .body(buildErrorResponse(ErrorCode.INVALID_INPUT_VALUE, errorMessage, request.getRequestURI()));
    }

    /**
     * 3. 예상치 못한 시스템 에러 (최후의 보루)
     * NullPointerException 등 우리가 예상하지 못한 에러가 발생했을 때 500 응답을 줍니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("Exception : {}", e.getMessage()); // 개발자는 로그로 원인을 확인하고

        // 클라이언트에게는 "서버 오류"라고만 알려줍니다 (보안상 상세 내용 숨김)
        return ResponseEntity
                .internalServerError()
                .body(buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, "알 수 없는 에러가 발생했습니다.", request.getRequestURI()));
    }

    // ErrorResponse 생성 편의 메서드
    private ErrorResponse buildErrorResponse(ErrorCode errorCode, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getStatus().value())
                .error(errorCode.getStatus().name())
                .code(errorCode.getCode())
                .message(message)
                .path(path)
                .build();
    }

    /**
     * 4. 스프링 시큐리티 권한 부족 에러 처리 (@PreAuthorize 실패 시)
     * 팀의 공통 ErrorResponse 포맷으로 403 Forbidden 응답을 줌.
     */

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e, HttpServletRequest request) {
        log.warn("AccessDeniedException : {}", e.getMessage());

        // 기존에 만들어둔 FORBIDDEN_ADMIN(E004, "권한이 없습니다.") 에러 코드를 재사용
        ErrorCode errorCode = ErrorCode.FORBIDDEN_ADMIN;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(buildErrorResponse(errorCode, errorCode.getMessage(), request.getRequestURI()));
    }


}