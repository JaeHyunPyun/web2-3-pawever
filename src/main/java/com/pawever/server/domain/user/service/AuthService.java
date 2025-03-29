package com.pawever.server.domain.user.service;

import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.user.dto.internal.LoginSecurityMailSendDto;
import com.pawever.server.domain.user.dto.request.AuthLoginRequestDto;
import com.pawever.server.domain.user.dto.request.AuthPreLoginRequestDto;
import com.pawever.server.domain.user.dto.response.UserResponseDto;
import com.pawever.server.domain.user.jwt.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Encoder;
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
    private final MailSendService mailSendService;
    private final ClientInfoResolver clientInfoResolver;

    @Value("${spring.jwt.accessTokenExpirationTime}")
    private Long accessTokenExpiredMs;

    @Value("${spring.jwt.refreshTokenExpirationTime}")
    private Long refreshTokenExpiredMs;

    @Transactional
    public HttpHeaders login(AuthLoginRequestDto authLoginRequestDto, HttpServletRequest request) {

        // refactor : null 처리 코드 줄이기
        if(authLoginRequestDto == null){
            log.error("회원가입/로그인실패 : AuthRequestDto null");
            throw new CustomException(ResponseCodeEnum.MISSING_REQUIRED_FIELDS);
        }
        UserResponseDto userResponseDto = null;
        boolean isNewUser = false;

        // 1. 클라이언트 검증
        verifyClient(authLoginRequestDto);

        if(authLoginRequestDto.getSocialLoginUuid() != null) {
            // 2. authRequestDto의 uuid를 기준으로 User 테이블에서 사용자 조회
            userResponseDto = userService.getUserInfoByUuid(authLoginRequestDto.getSocialLoginUuid());
        }

        // 3. userResponseDto 값이 null이면 회원가입 진행
        if(userResponseDto == null){
            isNewUser = true;
            userResponseDto = userService.saveNewUser(
                userService.createNewUser(authLoginRequestDto, request)
            );
        }else if(Boolean.TRUE.equals(userResponseDto.getIsDeleted())){
            // 4. userResponseDto 에서 isdeleted가 true면 기존 회원정보 hardDelete 후 재가입
            isNewUser = true;

            // 기존 회원정보 삭제
            userService.hardDeleteUserByUuid(userResponseDto);

            // 재가입
            userResponseDto = userService.saveNewUser(
                userService.createNewUser(authLoginRequestDto, request)
            );
        }

        // 회원가입후 최초 로그인이 아닌 경우
        if(!isNewUser){
            // 4. 사용자 로그인시 이전 로그인 대비 IP 변경시 메일 전송
            String userEmail = userResponseDto.getEmail();
            String lastLoginIp = userResponseDto.getLastLoginIp();
            String currentLoginIp = clientInfoResolver.getClientIp(request);

            if(!currentLoginIp.equals(lastLoginIp) && userEmail!=null){
                log.info("lastLoginIp : {}", lastLoginIp );
                log.info("currentLoginIp : {}", currentLoginIp );
                LoginSecurityMailSendDto loginSecurityMailSendDto = LoginSecurityMailSendDto
                    .builder()
                    .emailAddr(userEmail)
                    .userName(userResponseDto.getName())
                    .build();

                mailSendService.sendLoginSecurityMail(loginSecurityMailSendDto, request);
            }

            // 5. 사용자 현재 접속 IP로 DB 정보 업데이트
            // 접속 ip 알 수 없는 경우이면서 기존 IP와 동일한 경우가 아닌 경우 업데이트
            if(!currentLoginIp.equalsIgnoreCase("UNKNOWN") && !currentLoginIp.equals(lastLoginIp)){
                userService.updateUserIp(currentLoginIp, userResponseDto.getUserId());
            }

            // 6. 사용자 로그인시 이전 로그인 대비 위도/경도 변경시 db에 반영
            userService.updateLocationIfChanged(userResponseDto.getUserId(), authLoginRequestDto);
        }


        // 7. Access/Refresh Token 생성
        String accessToken = jwtUtil.createJwt("access", userResponseDto, accessTokenExpiredMs);
        String refreshToken = jwtUtil.createJwt("refresh", userResponseDto, refreshTokenExpiredMs);

        // 8. Refresh토큰 Redis에 저장
        refreshTokenService.saveRefreshToken(refreshToken, userResponseDto.getName());

        return createHttpHeader(accessToken, refreshToken);
    }

    public String preLogin(AuthPreLoginRequestDto authPreLoginRequestDto){
        // 만료시간 테스트용으로 10시간 설정
        // todo 10분으로 변경
        return jwtUtil.createJwt("preLogin", authPreLoginRequestDto, 10*60*60*1000L);
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
    private void verifyClient(AuthLoginRequestDto authLoginRequestDto){

        String codeVerifier = authLoginRequestDto.getCodeVerifier();
        String preLoginJwt = authLoginRequestDto.getPreLoginJwt();

        // 클라이언트 검증 절차 진행
        if(codeVerifier == null || preLoginJwt == null) {
            log.error("회원가입/로그인 실패 : codeVerifier or preLoginJwt null");
            throw new CustomException(ResponseCodeEnum.MISSING_REQUIRED_FIELDS);
        }

        String codeChallenge = jwtUtil.getCodeChallenge(preLoginJwt);
        String codeChallengeMethod = jwtUtil.getCodeChallengeMethod(preLoginJwt);

        if(codeChallenge == null || codeChallengeMethod == null) {
            log.error("회원가입/로그인 실패 : codeChallenge or codeChallengeMethod null");
            throw new CustomException(ResponseCodeEnum.MISSING_REQUIRED_FIELDS);
        }

        if(codeChallengeMethod.equals("plain")){
            if(!codeChallenge.equals(codeVerifier)){
                throw new CustomException(ResponseCodeEnum.UNIDENTIFIED_CLIENT);
            }
        }else if(codeChallengeMethod.equals("S256")){
            if(!generateCodeChallenge(codeVerifier).equals(codeChallenge)){
                throw new CustomException(ResponseCodeEnum.UNIDENTIFIED_CLIENT);
            }
        }else{
            throw new CustomException(ResponseCodeEnum.UNSUPPORTED_HASH_ALGORITHM);
        }
    }

    private String generateCodeChallenge(String codeVerifier) {

        // 1. ASCII 인코딩
        byte[] codeVerifierBytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);

        // 2. SHA-256 해시 계산
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(ResponseCodeEnum.UNSUPPORTED_HASH_ALGORITHM);
        }

        byte[] codeVerifierHash = messageDigest.digest(codeVerifierBytes);

        // 3. BASE64URL 인코딩
        Encoder base64URLEncoder = Base64.getUrlEncoder().withoutPadding();

        // 4. CodeChallenge 변환 결과 반환
        return base64URLEncoder.encodeToString(codeVerifierHash);
    }
}