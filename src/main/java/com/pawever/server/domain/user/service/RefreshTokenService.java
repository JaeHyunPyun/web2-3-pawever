package com.pawever.server.domain.user.service;

import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.user.entity.redis.RefreshToken;
import com.pawever.server.domain.user.jwt.JwtProperties;
import com.pawever.server.domain.user.jwt.JwtUtil;
import com.pawever.server.domain.user.repository.redis.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final JwtUtil jwtUtil;


    @Transactional
    public void saveRefreshToken(String refreshToken, String name) {
        RefreshToken token = RefreshToken.builder()
            .refreshToken(refreshToken)
            .name(name)
            .ttl(jwtProperties.getRefreshTokenExpirationTime()/1000)
            .build();
        refreshTokenRepository.save(token);
        log.info("리프레시 토큰 저장: 토큰 - {}, 유저명 - {}", refreshToken, name);
    }

    @Transactional
    public void removeRefreshToken(String refreshToken) {
        refreshTokenRepository.findRefreshTokenByRefreshToken(refreshToken)
            .ifPresent(token -> refreshTokenRepository.delete(token));
    }

    public RefreshToken getRedisRefreshToken(String refreshToken) {
        return refreshTokenRepository.findRefreshTokenByRefreshToken(refreshToken)
            .orElseThrow(()-> new CustomException(ResponseCodeEnum.REFRESH_TOKEN_NOT_FOUND));
    }

    public String getValidRefreshToken(HttpServletRequest request) {

        // 1. request로부터 RefreshToken 가져오기
        String refreshToken = getRequestRefreshToken(request);

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
        getRedisRefreshToken(refreshToken);

        return refreshToken;
    }

    public String getRequestRefreshToken(HttpServletRequest request) {

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

