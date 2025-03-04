package com.pawever.server.domain.user.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void writeAsJsonResponse(HttpServletResponse response, Object responseObject) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String jsonResponse = objectMapper.writeValueAsString(responseObject);
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
        } catch (IOException e) {
            throw new RuntimeException("JSON 응답 변환 중 오류 발생", e);
        }
    }
}
