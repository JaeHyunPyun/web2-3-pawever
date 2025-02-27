package com.pawever.server.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"isSuccess", "status", "code", "data"})
public class ApiResponse {
    @JsonProperty("isSuccess")
    private Boolean isSuccess;
    @JsonProperty("status")
    private String status;
    @JsonProperty("code")
    private String code;
    @JsonInclude(JsonInclude.Include.NON_NULL) //필드 값이 null 이면 JSON 응답에서 제외됨.
    @JsonProperty("data")
    private Object data;

    public static ApiResponse success(ResponseCodeEnum responseCodeEnum, Object data){
        return  new ApiResponse(true,responseCodeEnum.getStatus().name(),responseCodeEnum.getCode(),data);
    }
    public static ApiResponse success(ResponseCodeEnum responseCodeEnum){
        return success(responseCodeEnum,null);
    }
    public static ApiResponse fail(ResponseCodeEnum responseCodeEnum){
        return fail(responseCodeEnum,null);
    }

    public static ApiResponse fail(ResponseCodeEnum responseCodeEnum, Object data){
        return new ApiResponse(false,responseCodeEnum.getStatus().name(),responseCodeEnum.getCode(),data);
    }


    
}
