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

        UserResponseDto userResponseDto = null;

        if(authRequestDto == null){
            log.error("회원가입실패 : AuthRequestDto null");
            throw new CustomException(ResponseCodeEnum.MISSING_REQUIRED_FIELDS);
        }

        if(authRequestDto.getSocialLoginUuid() != null) {
            log.info("로그인 시도 - 소셜로그인uuid: {}", authRequestDto.getSocialLoginUuid());
            // 1. authRequestDto의 uuid를 기준으로 User 테이블에서 사용자 조회
            userResponseDto = userService.getUserInfoByUuid(authRequestDto.getSocialLoginUuid());
        }

        // 2. userResponseDto 값이 null이면 회원가입 진행
        if(userResponseDto == null){
            userResponseDto = userService.saveNewUser(
                userService.createNewUser(authRequestDto)
            );
        }else if(Boolean.TRUE.equals(userResponseDto.getIsDeleted())){
            // 3. userResponseDto 에서 isdeleted가 true면 기존 회원정보 hardDelete 후 재가입
            // 기존 회원정보 삭제
            userService.hardDeleteUserByUuid(userResponseDto);

            // 재가입
            userResponseDto = userService.saveNewUser(
                userService.createNewUser(authRequestDto)
            );
        }

        // 4. 사용자 로그인시 위도/경도 변경시 db에 반영
        userService.updateLocationIfChanged(userResponseDto.getUserId(), authRequestDto);

        // 5. Access/Refresh Token 생성
        String accessToken = jwtUtil.createJwt("access", userResponseDto, accessTokenExpiredMs);
        String refreshToken = jwtUtil.createJwt("refresh", userResponseDto, refreshTokenExpiredMs);

        // 6. Refresh토큰 Redis에 저장
        refreshTokenService.saveRefreshToken(refreshToken, userResponseDto.getName());

        // 7. 컨트롤러로 반환
        return createHttpHeader(accessToken, refreshToken);
    }

    public HttpHeaders refreshTokens(HttpServletRequest request){

        // 1. request로부터 유효한 refreshToken 가져오기
        // 쿠키나 Refresh 토큰이 없는 경우 400(BAD_REQUEST) 반환
        // 토큰 만료시 401(UNAUTHORIZED) 반환
        // Refresh 토큰이 아닌경우 400(BAD_REQUEST) 반환
        // 존재하지 않는 refresh 토큰이라면 탈취된 토큰으로 간주하고 401(UNAUTHORIZED) 반환
        String refreshToken = refreshTokenService.getValidRefreshToken(request);

        // 2. Refresh토큰의 사용자 정보 추출
        UserResponseDto userResponseDto = jwtUtil.getUserResponseDto(refreshToken);

        // 3. AccessToken, RefreshToken 재발급(Refresh Rotate)
        String accessToken = jwtUtil.createJwt("access", userResponseDto, accessTokenExpiredMs);
        String newRefreshToken = jwtUtil.createJwt("refresh", userResponseDto, refreshTokenExpiredMs);

        // 4. redis에 갱신된 RefreshToken 저장
        // 1) 기존 RefreshToken 제거
        refreshTokenService.removeRefreshToken(refreshToken);
        // 2) 새로운 RefreshToken 저장
        refreshTokenService.saveRefreshToken(newRefreshToken, userResponseDto.getName());

        // 5. 컨트롤러로 반환
        return createHttpHeader(accessToken, newRefreshToken);
    }

    private HttpHeaders createHttpHeader(String accessToken, String refreshToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken); // 헤더에 access 토큰 추가
        httpHeaders.set(HttpHeaders.SET_COOKIE, createCookieHeader("refresh", refreshToken)); // 쿠키 추가
        return httpHeaders;
    }

    private String createCookieHeader(String name, String value) {
        // 프론트 로컬에서 임시로 접근 가능하도록 설정
        return name + "=" + value + "; Path=/; HttpOnly; Secure; SameSite=None;  Max-Age=" + (refreshTokenExpiredMs);

        // 프론트 배포시 적용할 설정(https에서만 쿠키 전달 + 크로스 사이트 요청에 대해 쿠키 전송 허용)
        //return name + "=" + value + "; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=" + (refreshTokenExpiredMs);


    }



}