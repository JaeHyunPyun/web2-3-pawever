package com.pawever.server.common.handler;


import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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

    //request 관련 error
    @ExceptionHandler({
            MethodArgumentNotValidException.class, //json body (requestpart의 body, requestBody의 body)의 필드가 설정한 유효값을 만족시키지 않거나, 필수값이 누락됨.
            HttpMessageNotReadableException.class, //json body (requestpart의 body, requestBody의 body)의 필드 type이 잘못됨.
            MissingServletRequestPartException.class,   // required인 requestpart가 없음.
            MissingServletRequestParameterException.class, // requried인 request param이 없음.
            MethodArgumentTypeMismatchException.class //request parameter, pathVariable의 type이 잘못됨.
    })
    public ResponseEntity<ApiResponse> handleMissingServletRequestPartException(Exception e){
        log.error("[Exception] CODE : {} |  MESSAGE : {}", ResponseCodeEnum.INVALID_REQUEST_ARGUMENT.getCode(), e.getMessage());
        return ResponseEntity.status(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT.getStatus()).body(ApiResponse.fail(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT));
    }


}
