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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
        boolean isGetAllPostRequest = path.equals("/api/community/posts") &&
                (method.equalsIgnoreCase("GET"));
        boolean isGetPostRequest = path.matches("^/api/community/posts(/\\d+)?$") &&
                method.equalsIgnoreCase("GET");
        boolean isGetPetMainRequest = path.equals("/api/animals") &&
                (method.equalsIgnoreCase("GET"));
        boolean isGetPetRequest = path.equals("/api/animals/search") &&
                (method.equalsIgnoreCase("GET"));
        boolean allRequestAllowance = path.startsWith("/");  // 로그인 기능 완전히 구현할때까지 우선 모두 허용
//        return allRequestAllowance;
        return isLoginRequest || isTokenRefreshRequest || isGetAllPostRequest || isGetPostRequest || isGetPetMainRequest || isGetPetRequest;
    }

    // JWTUtil 클래스에 정의해두었던 Jwt토큰 검증에 사용되는 메서드들을 사용해야하므로
    // JWTUtil 클래스를 멤버필드로 선언하고 주입받아야함
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

//        // 1. AccessToken 꺼내기
//        String accessTokenWithBearer= request.getHeader("Authorization");
//        System.out.println("accessTokenWithBearer: " + accessTokenWithBearer);
//
//        // 2. 토큰이 없다면 400(BAD_REQUEST) 발생시키고 필터 종료
//        if (accessTokenWithBearer == null || !accessTokenWithBearer.startsWith("Bearer ")) {
//            log.error("[JWTFilter] 액세스 토큰이 없습니다.");
//            throw new CustomException(ResponseCodeEnum.ACCESS_TOKEN_NULL);
//        }
//
//        // 2-1. 토큰에서 Bearer 제거하고 실질적인 토큰값을 가져오기
//        String accessToken = accessTokenWithBearer.split(" ")[1];
//
//        // 3. 토큰이 있다면 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
//        // todo 프론트 측과 만료시 반환하는 응답 코드 및 메세지 조율 필요(400 또는 401)
//        // 토큰 만료시 401(UNAUTHORIZED) 반환
//        try {
//            jwtUtil.isExpired(accessToken);
//        } catch (ExpiredJwtException e) {
//            log.error("[JWTFilter] 액세스 토큰이 만료되었습니다.");
//            throw new CustomException(ResponseCodeEnum.JWT_TOKEN_EXPIRED);
//        }
//
//        // 4. 토큰 category 가 access인지 확인
//        // Refresh 토큰이 주어진 경우 BAD_REQUEST(400) 반환
//        String category = jwtUtil.getCategory(accessToken);
//        if (!category.equals("access")) {
//            log.error("[JWTFilter] 액세스 토큰 타입이 아닙니다.");
//            throw new CustomException(ResponseCodeEnum.TOKEN_CATEGORY_MISMATCH);
//        }
        // 1. 유효한 AccessToken 가져오기
        String validAccessToken = accessTokenService.getValidRequestAccessToken(request);

        // 2. accesstoken에 대한 검증이 완료되었으므로 accesstoken에서 socialLoginUuid, role 값을 가져오기
        String socialLoginUuid = jwtUtil.getSocialLoginUuid(validAccessToken);
        Role role = jwtUtil.getRole(validAccessToken);

//        // 3. api에서 sociluuid 가져오는 것 테스트
//        System.out.println("test get uuid from request : " + accessTokenService.getRequestSocialLoginUuid(request));


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
