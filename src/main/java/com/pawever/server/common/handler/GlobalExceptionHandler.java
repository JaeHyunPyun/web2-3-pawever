package com.pawever.server.common.handler;


import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Arrays;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //custom exception
    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ApiResponse> handleCustomException(CustomException e){
        log.error("[{}] code:{} / code message:{}", e.getResponseCodeEnum().name(),e.getResponseCodeEnum().getCode(), e.getMessage());
        System.out.println("e.getResponseCodeEnum().name() = " + e.getResponseCodeEnum().name());
        return ResponseEntity.status(e.getResponseCodeEnum().getStatus()).body(ApiResponse.fail(e.getResponseCodeEnum()));

    }

    //처리되지 못한 기타 exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handlingException(Exception e) {

        log.error("[Exception] CODE : {} |  MESSAGE : {}", ResponseCodeEnum.UNKNOWN_SERVER_ERROR.getCode(),e.getMessage());

        return ResponseEntity.status(ResponseCodeEnum.UNKNOWN_SERVER_ERROR.getStatus()).body(ApiResponse.fail(ResponseCodeEnum.UNKNOWN_SERVER_ERROR));

    }

    //@Valid 관련 에러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleInvalidArgumentException(MethodArgumentNotValidException e){

        log.error("[Exception] CODE : {} |  MESSAGE : {}", ResponseCodeEnum.INVALID_REQUEST_ARGUMENT.getCode(), e.getMessage());
        return ResponseEntity.status(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT.getStatus()).body(ApiResponse.fail(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT));
    }

     //JSON 파싱 오류 (클라이언트가 빈 JSON 또는 잘못된 JSON을 보냈을 때)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("[Exception] CODE : {} | MESSAGE : {}", ResponseCodeEnum.INVALID_REQUEST_ARGUMENT.getCode(), ex.getMessage());
        return ResponseEntity.status(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT.getStatus())
                .body(ApiResponse.fail(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT));
    }


     //필수 요청 값이 없는 경우 (예: @RequestPart("request") 누락)
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiResponse> handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
        log.error("[Exception] CODE : {} | MESSAGE : {}", ResponseCodeEnum.INVALID_REQUEST_ARGUMENT.getCode(), ex.getMessage());
        return ResponseEntity.status(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT.getStatus()).body(ApiResponse.fail(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT));
    }


}
