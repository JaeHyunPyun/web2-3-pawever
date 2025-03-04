package com.pawever.server.domain.user.service;

import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.user.dto.request.AuthRequestDto;
import com.pawever.server.domain.user.dto.response.UserProfileResponseDto;
import com.pawever.server.domain.user.dto.response.UserResponseDto;
import com.pawever.server.domain.user.entity.jpa.User;
import com.pawever.server.domain.user.enums.Role;
import com.pawever.server.domain.user.jwt.JwtUtil;
import com.pawever.server.domain.user.repository.jpa.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenService accessTokenService;
    private final JwtUtil jwtUtil;

    public UserResponseDto getUserInfoByUuid(String socialLoginUuid){

        Optional<User> foundUser = userRepository.findBySocialLoginUuid(socialLoginUuid);
        return foundUser.map(user -> UserResponseDto.builder()
            .userId(user.getUserId())
            .socialLoginUuid(user.getSocialLoginUuid())
            .name(user.getName())
            .role(user.getRole())
            .isDeleted(user.getIsDeleted())
            .build()).orElse(null);
    }

    public User createNewUser(AuthRequestDto authRequestDto) {
        return User.builder()
            .name(authRequestDto.getName())
            .email(authRequestDto.getEmail())
            .profileImageUrl(authRequestDto.getProfileImageUrl())
            .socialLoginUuid(authRequestDto.getSocialLoginUuid())
            .socialLoginProvider(authRequestDto.getSocialLoginProvider())
            .latitude(authRequestDto.getLatitude())
            .longitude(authRequestDto.getLongitude())
            .role(Role.ROLE_USER)
            .isDeleted(false)
            .build();
    }

    @Transactional
    public UserResponseDto saveNewUser(User user) {

        try {
            User savedUser = userRepository.save(user);

            log.info("회원가입 완료 : 회원ID - {}", user.getUserId());

            return UserResponseDto.builder()
                .userId(savedUser.getUserId())
                .socialLoginUuid(savedUser.getSocialLoginUuid())
                .name(savedUser.getName())
                .role(savedUser.getRole())
                .build();
        } catch (DataIntegrityViolationException e) {
            log.error("회원가입실패 : 회원ID - {}, 이유 - {}", user.getUserId(), e.getMessage());
            throw new CustomException(ResponseCodeEnum.MISSING_REQUIRED_FIELDS);
        }
    }

    @Transactional
    public void softDeleteUserByUuid(HttpServletRequest request) {
        // 1. request로부터 refresh토큰 추출(logout 단계에서 유효성 검증했으므로 유효성 체크 생략)
        String refreshToken = refreshTokenService.getRequestRefreshToken(request);

        // 2. refresh 토큰에서 유저 Uuid 추출
        String userUuid = jwtUtil.getSocialLoginUuid(refreshToken);

        // 3. 유저가 존재하는지 확인 후 Soft Delete 실행
        if (userRepository.findBySocialLoginUuid(userUuid).isEmpty()) {
            throw new CustomException(ResponseCodeEnum.USER_NOT_FOUND);
        }

        // 4. Soft Delete 실행
        userRepository.softDeleteByUuid(userUuid);
    }

    @Transactional
    public void hardDeleteUserByUuid(UserResponseDto userResponseDto) {
        // Hard Delete 실행
        userRepository.hardDeleteByUuid(userResponseDto.getSocialLoginUuid());
    }


    public void logout(HttpServletRequest request, HttpServletResponse response){

        // 1. Request로부터 Refresh Token 가져와서 유효성 검증
        String refreshToken = refreshTokenService.getValidRefreshToken(request);

        // 2. 유효한 Refresh Token을 제거 -> 토큰 갱신 방지
        refreshTokenService.removeRefreshToken(refreshToken);

        // 3. 쿠키 초기화
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);  // HttpOnly 플래그 추가 (JavaScript에서 접근 불가)
        cookie.setSecure(false);   // (HTTP 환경에서는 `Secure` 설정을 false로 해주세요)

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);  // 204 응답코드 전송

    }

    public UserProfileResponseDto getUserProfiles(HttpServletRequest request) {
        String userSocialLoginUuid = accessTokenService.getRequestSocialLoginUuid(request);

        // 유저 정보 조회
        Optional<User> optionalUser = userRepository.findBySocialLoginUuid(userSocialLoginUuid);

        return optionalUser.map(user -> UserProfileResponseDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .build())
            .orElseThrow(() -> new CustomException(ResponseCodeEnum.USER_NOT_FOUND));
    }

}
