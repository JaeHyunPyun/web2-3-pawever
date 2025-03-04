package com.pawever.server.domain.user.config;

import com.pawever.server.domain.user.handler.FilterExceptionHandler;
import com.pawever.server.domain.user.jwt.CustomLogoutFilter;
import com.pawever.server.domain.user.jwt.JwtFilter;
import com.pawever.server.domain.user.jwt.JwtUtil;
import com.pawever.server.domain.user.service.RefreshTokenService;
import com.pawever.server.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf((auth) -> auth.disable());
        http
            .formLogin((auth) -> auth.disable());
        http
            .httpBasic((auth) -> auth.disable());
        http
            .logout((auth) -> auth.disable());
        // 경로별 인가 처리
        http
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/tokens").permitAll()  // 로그인 요청시 허용
                .requestMatchers("/**").permitAll()  // 로그인 기능 완전히 구현할때까지 우선 모두 허용
//                .requestMatchers("/api/auth/refreshedtokens").permitAll()  // 토큰 재발급 요청
//                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated()
            );
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

        http
            .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        http
            .addFilterAt(new CustomLogoutFilter(jwtUtil, refreshTokenService, userService), LogoutFilter.class);
        http
            .addFilterBefore(new FilterExceptionHandler(), LogoutFilter.class);


        return http.build();
    }
}
