package com.example.commerce.global.common;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommonResponseDTO<T> {
    private final LocalDateTime timestamp;  //성공 시각
    private final int status;               //상태 코드 (200, 201 등)
    private final String name;              //Success 코드 이름
    private final T data;                   //response DTO
    private final String message;           //개별 메시지
}
