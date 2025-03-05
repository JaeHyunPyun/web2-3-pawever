package com.pawever.server.domain.user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
            .allowedOriginPatterns("*")         // 모든 요청에 대해서 CORS 허용
//            .allowedOriginPatterns("https://pawever.netlify.app")    // 프론트 도메인에 대해서 CORS 허용
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드 지정
            .allowedHeaders("*")                                     // 모든 헤더 허용
            .allowCredentials(true);                                // 인증 정보 포함 가능
    }
}
