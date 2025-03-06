package com.pawever.server.domain.user.jwt;

import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.user.dto.response.CustomUserDetails;
import com.pawever.server.domain.user.dto.response.UserAuthInfoDto;
import com.pawever.server.domain.user.enums.Role;
import com.pawever.server.domain.user.service.AccessTokenService;
import com.pawever.server.domain.user.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AccessTokenService accessTokenService;

    // Jwt 검증이 필요없는 요청들은 filter 적용 제외
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 🔹 기존 인증 제외 조건: POST, PUT /api/auth/tokens
        boolean isLoginRequest = path.equals("/api/auth/tokens") &&
            (method.equalsIgnoreCase("POST"));
        boolean isTokenRefreshRequest = path.equals("/api/auth/refreshedtokens") &&
            (method.equalsIgnoreCase("POST"));
        boolean isLogoutRequest = path.equals("/api/auth/tokens") &&
            (method.equalsIgnoreCase("DELETE"));
        boolean isWithdrawRequest = path.equals("/api/users/profiles") &&
            (method.equalsIgnoreCase("DELETE"));
        boolean isGetAllPostRequest = path.equals("/api/community/posts") &&
                (method.equalsIgnoreCase("GET"));
        boolean isGetPostRequest = path.matches("^/api/community/posts(/\\d+)?$") &&
                method.equalsIgnoreCase("GET");
        boolean isGetPetMainRequest = path.startsWith("/api/animals") &&
                (method.equalsIgnoreCase("GET"));
        boolean isGetPetRequest = path.startsWith("/api/animals/search") &&
                (method.equalsIgnoreCase("GET"));
        //swagger 경로 허용
        boolean swaggerRequest1 = path.startsWith("/swagger-ui");
        boolean swaggerRequest2 = path.startsWith("/v3/api-docs");
        boolean swaggerRequest3 = path.startsWith("/swagger-resources");
        boolean swaggerRequest4 = path.startsWith("/swagger-ui.html");
        boolean swaggerRequest5 = path.startsWith("/swagger-config");
        boolean swaggerRequest6 = path.startsWith("/docs");
        boolean allRequestAllowance = path.startsWith("/");  // 로그인 기능 완전히 구현할때까지 우선 모두 허용
//        return allRequestAllowance;
        return isLoginRequest || isTokenRefreshRequest ||isLogoutRequest || isWithdrawRequest || isGetAllPostRequest || isGetPostRequest || isGetPetMainRequest || isGetPetRequest || swaggerRequest1 || swaggerRequest2 || swaggerRequest3 || swaggerRequest4 || swaggerRequest5 || swaggerRequest6;
    }

    // JWTUtil 클래스에 정의해두었던 Jwt토큰 검증에 사용되는 메서드들을 사용해야하므로
    // JWTUtil 클래스를 멤버필드로 선언하고 주입받아야함
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. 유효한 AccessToken 가져오기
        String validAccessToken = accessTokenService.getValidRequestAccessToken(request);

        // 2. accesstoken에 대한 검증이 완료되었으므로 accesstoken에서 socialLoginUuid, role 값을 가져오기
        String socialLoginUuid = jwtUtil.getSocialLoginUuid(validAccessToken);
        Role role = jwtUtil.getRole(validAccessToken);

        // 3. UserAuthInfoDto에 name과 role을 넣어주고 CustomUserDetails에 담고
        CustomUserDetails customUserDetails = new CustomUserDetails(
            UserAuthInfoDto.builder()
                .socialLoginUuid(socialLoginUuid)
                .role(role)
                .build()
        );

        // 4. Spring Security 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);

    }
}
