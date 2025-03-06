package com.pawever.server.domain.user.service;


import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.user.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccessTokenService {

    private final JwtUtil jwtUtil;

    public String getValidRequestAccessToken(HttpServletRequest request) {

        // jwt 필터에서 검증할때 사용
        // 1. AccessToken 꺼내기
        String accessTokenWithBearer= request.getHeader("Authorization");
        System.out.println("accessTokenWithBearer: " + accessTokenWithBearer);

        // 2. 토큰이 없다면 400(BAD_REQUEST) 발생시키고 필터 종료
        if (accessTokenWithBearer == null || !accessTokenWithBearer.startsWith("Bearer ")) {
            log.error("[JWTFilter] 액세스 토큰이 없습니다.");
            throw new CustomException(ResponseCodeEnum.ACCESS_TOKEN_NULL);
        }

        // 2-1. 토큰에서 Bearer 제거하고 실질적인 토큰값을 가져오기
        String accessToken = accessTokenWithBearer.split(" ")[1];

        // 3. 토큰이 있다면 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        // todo 프론트 측과 만료시 반환하는 응답 코드 및 메세지 조율 필요(400 또는 401)
        // 토큰 만료시 401(UNAUTHORIZED) 반환
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            log.error("[JWTFilter] 액세스 토큰이 만료되었습니다.");
            throw new CustomException(ResponseCodeEnum.JWT_TOKEN_EXPIRED);
        }

        // 4. 토큰 category 가 access인지 확인
        // Refresh 토큰이 주어진 경우 BAD_REQUEST(400) 반환
        String category = jwtUtil.getCategory(accessToken);
        if (!category.equals("access")) {
            log.error("[JWTFilter] 액세스 토큰 타입이 아닙니다.");
            throw new CustomException(ResponseCodeEnum.TOKEN_CATEGORY_MISMATCH);
        }
        return accessToken;
    }

    public String getRequestAccessToken(HttpServletRequest request) {
        // 로그인 성공후 api에서 acceessToken내 정보 추출시 사용
        // 1. AccessToken 꺼내기
        String accessTokenWithBearer= request.getHeader("Authorization");

        // 2. 토큰이 없다면 400(BAD_REQUEST) 발생시키고 필터 종료
        if (accessTokenWithBearer == null || !accessTokenWithBearer.startsWith("Bearer ")) {
            log.error("[JWTFilter] 액세스 토큰이 없습니다.");
            throw new CustomException(ResponseCodeEnum.ACCESS_TOKEN_NULL);
        }

        // 2-1. 토큰에서 Bearer 제거하고 실질적인 토큰값을 가져오기
        String accessToken = accessTokenWithBearer.split(" ")[1];

       return accessToken;
    }

    public String getRequestSocialLoginUuid(HttpServletRequest request) {
        String accessToken = getRequestAccessToken(request);
        return jwtUtil.getSocialLoginUuid(accessToken);
    }



}
