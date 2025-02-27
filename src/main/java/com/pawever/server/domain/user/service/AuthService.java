package com.pawever.server.domain.user.service;

import com.pawever.server.domain.user.dto.request.AuthRequestDto;
import com.pawever.server.domain.user.dto.response.UserResponseDto;
import com.pawever.server.domain.user.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JWTUtil jwtUtil;
    private final UserService userService;

    @Value("${spring.jwt.accessToken_expiration_time}")
    private Long accessTokenExpiredMs;

    @Value("${spring.jwt.refreshToken_expiration_time}")
    private Long refreshTokenExpiredMs;

    @Transactional
    public HttpHeaders login(AuthRequestDto authRequestDto, HttpServletResponse response) {

        // 1. authRequestDto의 uuid를 기준으로 User 테이블에서 사용자 조회
        UserResponseDto userResponseDto = userService.getUserInfoByUuid(authRequestDto.getSocialLoginUuid());

        // 2. userResponseDto 값이 null이면 회원가입 진행
        if(userResponseDto == null){
            userResponseDto = userService.saveNewUser(
                userService.createNewUser(authRequestDto)
            );
        }

        // 3. Access/Refresh Token 생성
        String accessToken = jwtUtil.createJwt("access", userResponseDto, accessTokenExpiredMs);
        String refreshToken = jwtUtil.createJwt("refresh", userResponseDto, refreshTokenExpiredMs);

        // 4. Refresh토큰 Redis에 저장
        // 별도 메서드로 만들기

        // 5. 컨트롤러로 반환
        return createHttpHeader(accessToken, refreshToken);
    }


    private HttpHeaders createHttpHeader(String accessToken, String refreshToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("access", accessToken); // 헤더에 access 토큰 추가
        httpHeaders.set(HttpHeaders.SET_COOKIE, createCookieHeader("refresh", refreshToken)); // 쿠키 추가
        return httpHeaders;
    }

    private String createCookieHeader(String name, String value) {
        return name + "=" + value + "; HttpOnly; Max-Age=" + (60 * 5);

        // https 설정
//        return name + "=" + value + "; Path=/; HttpOnly; Secure;  Max-Age=" + (60 * 5);
    }
}