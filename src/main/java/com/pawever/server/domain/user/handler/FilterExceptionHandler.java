package com.pawever.server.domain.user.handler;

import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.user.response.FilterErrorResponse;
import com.pawever.server.domain.user.response.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

@Slf4j
public class FilterExceptionHandler extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        this.doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (CustomException e) {
            log.error("CustomException 발생: [{}] code:{} / message:{}",
                e.getResponseCodeEnum().name(), e.getResponseCodeEnum().getCode(), e.getMessage());

            sendErrorResponse(response, e);
        } catch (Exception e) {
            log.error("[Unhandled Exception] {}", e.getMessage(), e);
            sendErrorResponse(response, new CustomException(ResponseCodeEnum.UNKNOWN_SERVER_ERROR));
        }
    }

    private void sendErrorResponse(HttpServletResponse response, CustomException e) throws IOException {
        ResponseCodeEnum errorCode = e.getResponseCodeEnum();
        response.setStatus(errorCode.getStatus().value());

        FilterErrorResponse filterErrorResponse = FilterErrorResponse.fail(errorCode);

        ResponseUtil.writeAsJsonResponse(response, filterErrorResponse);
    }
}