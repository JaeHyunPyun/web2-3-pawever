package com.pawever.server.domain.user.response;

import com.pawever.server.common.response.ResponseCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FilterErrorResponse {
    private Boolean isSuccess;
    private String status;
    private String code;
    private Object data;

    public static FilterErrorResponse fail(ResponseCodeEnum responseCodeEnum) {
        return fail(responseCodeEnum, null);
    }

    public static FilterErrorResponse fail(ResponseCodeEnum responseCodeEnum, Object data) {
        return new FilterErrorResponse(
            false,
            responseCodeEnum.getStatus().name(),
            responseCodeEnum.getCode(),
            data
        );
    }
}