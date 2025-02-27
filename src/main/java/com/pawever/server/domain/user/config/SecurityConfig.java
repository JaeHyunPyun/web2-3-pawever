package com.pawever.server.domain.user.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf((auth) -> auth.disable());
        http
            .formLogin((auth) -> auth.disable());
        http
            .httpBasic((auth) -> auth.disable());
        // 경로별 인가 처리
        http
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers("/api/auth/tokens", "/").permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated());
        //인증 실패 시 401 Unauthorized 응답을 반환
        //접근 권한이 없을 때 403 Forbidden 응답을 반환
//        http
//            .exceptionHandling(exceptionHandling -> exceptionHandling
//                .authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
//                .accessDeniedHandler((request, response, accessDeniedException) -> response.sendError(
//                    HttpServletResponse.SC_FORBIDDEN, "Forbidden")));
        http
            .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
