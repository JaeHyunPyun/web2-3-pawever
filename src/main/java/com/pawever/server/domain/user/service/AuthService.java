package com.pawever.server.domain.user.service;

import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.user.dto.request.AuthRequestDto;
import com.pawever.server.domain.user.dto.response.UserResponseDto;
import com.pawever.server.domain.user.jwt.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @Value("${spring.jwt.accessTokenExpirationTime}")
    private Long accessTokenExpiredMs;

    @Value("${spring.jwt.refreshTokenExpirationTime}")
    private Long refreshTokenExpiredMs;

    @Transactional
    public HttpHeaders login(AuthRequestDto authRequestDto) {

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
        refreshTokenService.saveRefreshToken(refreshToken, userResponseDto.getName());

        // 5. 컨트롤러로 반환
        return createHttpHeader(accessToken, refreshToken);
    }

    public HttpHeaders refreshTokens(HttpServletRequest request){

        String refreshToken = null;

        // 1. request로부터 refreshToken 가져오기
        // 쿠키나 Refresh 토큰이 없는 경우 400(BAD_REQUEST) 반환
        refreshToken = getRefreshToken(request);

        System.out.println("refreshToken: " + refreshToken);
        // 2. refreshToken 만료 확인
        // 토큰 만료시 401(UNAUTHORIZED) 반환
        // todo 프론트 측과 만료시 반환하는 응답 코드 및 메세지 조율 필요(400 또는 401)
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            log.error("[JWTFilter] 리프레시 토큰이 만료되었습니다.");
            throw new CustomException(ResponseCodeEnum.JWT_TOKEN_EXPIRED);
        }

        // 3. 토큰 구분 확인(access or refresh)
        // Refresh 토큰이 아닌경우 400(BAD_REQUEST) 반환
        checkTokenCategory(refreshToken);

        // 4. redis 서버에 존재하는 refresh토큰인지 확인(Refresh Rotate)
        // 존재하지 않는 refresh 토큰이라면 탈취된 토큰으로 간주하고 401(UNAUTHORIZED) 반환
        refreshTokenService.getRefreshToken(refreshToken);

        // 5. Refresh토큰의 사용자 정보 추출
        UserResponseDto userResponseDto = jwtUtil.getUserResponseDto(refreshToken);

        // 6. AccessToken, RefreshToken 재발급(Refresh Rotate)
        String accessToken = jwtUtil.createJwt("access", userResponseDto, accessTokenExpiredMs);
        String newRefreshToken = jwtUtil.createJwt("refresh", userResponseDto, refreshTokenExpiredMs);

        // 7. redis에 갱신된 RefreshToken 저장
        // 1) 기존 RefreshToken 제거
        refreshTokenService.removeRefreshToken(refreshToken);
        // 2) 새로운 RefreshToken 저장
        refreshTokenService.saveRefreshToken(newRefreshToken, userResponseDto.getName());

        // 8. 컨트롤러로 반환
        return createHttpHeader(accessToken, newRefreshToken);
    }


    private HttpHeaders createHttpHeader(String accessToken, String refreshToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken); // 헤더에 access 토큰 추가
        httpHeaders.set(HttpHeaders.SET_COOKIE, createCookieHeader("refresh", refreshToken)); // 쿠키 추가
        return httpHeaders;
    }

    private String createCookieHeader(String name, String value) {
        return name + "=" + value + "; HttpOnly; Max-Age=" + (refreshTokenExpiredMs);

        // https 설정
//        return name + "=" + value + "; Path=/; HttpOnly; Secure;  Max-Age=" + (refreshTokenExpiredMs);
    }

    private String getRefreshToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if(cookies == null){
            // 쿠키가 존재하지 않는 경우 - 400(BAD REQUEST) 반환
            // 400 반환받으면 프론트에서는 재로그인 진행
            throw new CustomException(ResponseCodeEnum.COOKIE_NULL);
        }

        // 1. 쿠키에 저장된 Refresh Token 값을 가져오기
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("refresh")){
                return cookie.getValue();
            }
        }

        // 2. Refresh Token이 없는 경우 - 400(BAD REQUEST) 반환
        throw new CustomException(ResponseCodeEnum.REFRESH_TOKEN_NULL);
    }

    private void checkTokenCategory(String token){
        if(!jwtUtil.getCategory(token).equals("refresh")){
            // 400 코드 반환
            throw new CustomException(ResponseCodeEnum.TOKEN_CATEGORY_MISMATCH);
        }
    }

}