package com.pawever.server.domain.user.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
public class FilterExceptionHandler extends GenericFilterBean {

    private final ObjectMapper objectMapper;
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

        ApiResponse apiResponse = ApiResponse.fail(errorCode);

        response.setStatus(errorCode.getStatus().value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}