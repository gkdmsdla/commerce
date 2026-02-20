package com.example.commerce.global.common;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class CommonResponseHandler {

    /* !!!! 사용 방법 !!!!
    *. 위 아래 경우 모두 Controller 만 손대면 됩니닷
    *. 생성자 사용할 필요 Xx new 부분에 집중해서 해당 형태 그대로만 사용하시면 되어요 ^ _ ^ b

    1. 함수명 부분 바꿔주기
        old) public ResponseEntity<CreateOrderResponse> create( . . .) {
        new) public ResponseEntity<CommonResponseDTO<CreateOrderResponse>> create (. . .) {
    2. return 부분 바꿔주기
        old) ResponseEntity.status(HttpStatus.CREATED).body(response)
        new) CommonResponseHadnler.success(SuccessCode.ORDER_SUCCESSFUL, response)

    *. response 부분은 그대로 받아오셔야합니닷
       success 의 매개변수로 사용됩니다

    *. 만약 response 가 body 로 들어가지 않는 경우 (DTO 가 없는 login 또는 삭제...)

    1. 함수명 부분 바꿔주기
        old) public ResponseEntity<Void> delete( . . .) {
        new) public ResponseEntity<CommonResponseDTO<Void>> delete (. . .) {
    2. return 부분 바꿔주기
        old) ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        new) CommonResponseHadnler.success(SuccessCode.DELETE_SUCCESSFUL)

    */

    public static <T>ResponseEntity<CommonResponseDTO<T>> success(
            SuccessCode successCode,
            T data
    ){
        //반환값이 ResponseEntity<CommonResponseDTO<CreatePostResponse>> 순서로 가는거니까
        // <T> 제네릭 자리에 각 Domain 에서 사용하는 개별 response DTO 넣어서 사용하면 됨
        CommonResponseDTO<T> response = CommonResponseDTO.<T>builder()
                .timestamp(LocalDateTime.now())         //성공 시각
                .status(successCode.getStatus().value())//상태코드 (200, 201등)
                .name(successCode.getStatus().name())   //상태코드명
                .message(successCode.getMessage())      //저장된 메시지
                .data(data)                             //개별 DTO
                .build();

        return ResponseEntity.status(successCode.getStatus()).body(response);
    }



    // DTO 가 없는 경우 (오버로딩)
    public static ResponseEntity<CommonResponseDTO<Void>> success(
            SuccessCode successCode
    ) {
        return success(successCode, null);
    }

}
