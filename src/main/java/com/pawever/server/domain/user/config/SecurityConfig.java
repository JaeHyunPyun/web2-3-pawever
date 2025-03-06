package com.pawever.server.domain.user.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawever.server.domain.user.handler.FilterExceptionHandler;
import com.pawever.server.domain.user.handler.JWTAccessDeniedHandler;
import com.pawever.server.domain.user.jwt.CustomLogoutFilter;
import com.pawever.server.domain.user.jwt.JwtFilter;
import com.pawever.server.domain.user.jwt.JwtUtil;
import com.pawever.server.domain.user.service.AccessTokenService;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    //util
    private final JwtUtil jwtUtil;

    //service
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final AccessTokenService accessTokenService;

    //error handler
    private final JWTAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
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
                .requestMatchers("/error").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-config/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/swagger-resources/**").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/auth/refreshedtokens").permitAll()  // 토큰 재발급 요청
                .requestMatchers(HttpMethod.DELETE,"/api/auth/tokens").permitAll()  // 로그아웃 요청
                .requestMatchers(HttpMethod.DELETE,"/api/users/profiles").permitAll()  // 회원탈퇴 요청
                .requestMatchers(HttpMethod.GET, "/api/community/posts").permitAll()  // 게시글 조회
                .requestMatchers(HttpMethod.GET, "/api/community/posts/**").permitAll()  // 게시글 단건 조회
                .requestMatchers(HttpMethod.GET, "/api/animals").permitAll()  // 유기동물 조회(메인)
                .requestMatchers(HttpMethod.GET,"/api/animals/search/**").permitAll()  // 유기동물 조회(입양 동물 정보)
                .requestMatchers("/admin/**").hasRole("ADMIN") // 권한 설정
                .requestMatchers("/api/users/staff/**","/api/reservations/staff").hasRole("STAFF") // 권한 설정
                .anyRequest().authenticated()
            );

        //접근 권한이 없을 때 403 Forbidden 응답을 반환
        http
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedHandler(jwtAccessDeniedHandler));
        //session 설정
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //filter 설정
        http.addFilterBefore(new JwtFilter(jwtUtil, accessTokenService), UsernamePasswordAuthenticationFilter.class)
        .addFilterAt(new CustomLogoutFilter(jwtUtil, refreshTokenService, userService), LogoutFilter.class)
        .addFilterBefore(new FilterExceptionHandler(new ObjectMapper()), LogoutFilter.class);

        return http.build();
    }

    /**
     * CORS configuration for routes managed by Spring Security
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
//    // 프론트엔드 도메인 명시적으로 지정
//        configuration.setAllowedOrigins(List.of("https://pawever.netlify.app")); // 프론트 도메인
//        configuration.setAllowedOriginPatterns(List.of("*"));  // 프론트 도메인 받으면 수정예정
        configuration.setAllowedOrigins(List.of("https://pawever.netlify.app", "http://localhost:5175", "http://localhost:5173"));    // 특정 요청에 대해서 CORS 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // 프론트 도메인 받으면 수정예정
//        configuration.setAllowCredentials(false);  // 프론트 도메인 받으면 수정예정
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
