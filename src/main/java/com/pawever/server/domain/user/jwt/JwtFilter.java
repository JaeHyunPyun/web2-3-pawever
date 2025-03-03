package com.pawever.server.domain.user.jwt;

import com.pawever.server.domain.user.dto.response.CustomUserDetails;
import com.pawever.server.domain.user.dto.response.UserAuthInfoDto;
import com.pawever.server.domain.user.enums.Role;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    // Jwt 검증이 필요없는 요청들은 filter 적용 제외
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 🔹 기존 인증 제외 조건: POST, PUT /api/auth/tokens
        boolean isAuthTokenRequest = path.equals("/api/auth/tokens") &&
            (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT"));
//        boolean allRequestAllowance = path.startsWith("/");  // 로그인 기능 완전히 구현할때까지 우선 모두 허용
//        return isAuthTokenRequest || allRequestAllowance;
        return isAuthTokenRequest;
    }

    // JWTUtil 클래스에 정의해두었던 Jwt토큰 검증에 사용되는 메서드들을 사용해야하므로
    // JWTUtil 클래스를 멤버필드로 선언하고 주입받아야함
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. AccessToken 꺼내기
        String accessTokenWithBearer= request.getHeader("Authorization");
        System.out.println("accessTokenWithBearer: " + accessTokenWithBearer);

        // 2. 토큰이 없다면 권한이 없다고 반환하고 필터 종료하기
        if (accessTokenWithBearer == null || !accessTokenWithBearer.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("액세스 토큰이 없습니다.");
            log.info("[JWTFilter] 액세스 토큰이 없습니다.");
            return;
        }

        // 2-1. 토큰에서 Bearer 제거하고 실질적인 토큰값을 가져오기
        String accessToken = accessTokenWithBearer.split(" ")[1];

        // 3. 토큰이 있다면 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("액세스 토큰이 만료되었습니다.");
            log.error("[JWTFilter] 액세스 토큰이 만료되었습니다.");
            return;
        }

        // 4. 토큰 category 가 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);
        if (!category.equals("access")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("[JWTFilter] 액세스 토큰 타입이 아닙니다.");
            log.info("[JWTFilter] 액세스 액세스 토큰 타입이 아닙니다.");
            return;
        }

        // 5. accesstoken에 대한 검증이 완료되었으므로 accesstoken에서 socialLoginUuid, role 값을 가져오기
        String socialLoginUuid = jwtUtil.getSocialLoginUuid(accessToken);
        Role role = jwtUtil.getRole(accessToken);

        //6. UserAuthInfoDto에 name과 role을 넣어주고 CustomUserDetails에 담고
        CustomUserDetails customUserDetails = new CustomUserDetails(
            UserAuthInfoDto.builder()
                .socialLoginUuid(socialLoginUuid)
                .role(role)
                .build()
        );

        // 7. Spring Security 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);

    }
}
