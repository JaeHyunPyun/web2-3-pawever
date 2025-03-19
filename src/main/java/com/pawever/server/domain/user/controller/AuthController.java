package com.pawever.server.domain.user.controller;

import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.user.dto.request.AuthLoginRequestDto;
import com.pawever.server.domain.user.dto.request.AuthPreLoginRequestDto;
import com.pawever.server.domain.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "인증 API", description = "JWT 토큰 발급(로그인) 및 재발급 API")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/tokens")
    @Operation(summary = "로그인 및 JWT 토큰(액세스/리프레시) 발급 API")
    public ResponseEntity<ApiResponse> login(@RequestBody AuthLoginRequestDto authLoginRequestDto) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .headers(authService.login(authLoginRequestDto))
            .body(ApiResponse.success(ResponseCodeEnum.SUCCESS));
    }

    @GetMapping("/tokens/attempts")
    @Operation(summary = "로그인전 서버에 Code Challenge를 보내 Code Challenge를 담은 토큰을 반환받는 API")
    public ResponseEntity<ApiResponse> preLogin(
        @RequestParam(required = false) String codeChallenge,
        @RequestParam(required = false) String codeChallengeMethod
    ) {
        // codeChallenge가 없다면 400 BadRequest 반환
        if(codeChallenge==null||codeChallenge.isEmpty()){
            throw new CustomException(ResponseCodeEnum.CODE_CHALLENGE_NULL);
        }

        // 1. DTO 생성해서 codeChallenge, codeChallengeMethod 담기
        AuthPreLoginRequestDto authPreLoginRequestDto = AuthPreLoginRequestDto.builder()
            .codeChallenge(codeChallenge)
            .codeChallengeMethod(codeChallengeMethod).build();

        log.info("code Challenge: ", authPreLoginRequestDto.getCodeChallenge());
        log.info("code ChallengeMethod: ", authPreLoginRequestDto.getCodeChallengeMethod());
        // 2. codeChallenge, codeChallengeMethod를 담은 Jwt를 응답
        return ResponseEntity
            .ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, authService.preLogin(authPreLoginRequestDto)));
    }

    @PostMapping("/refreshedtokens")
    @Operation(summary = "JWT 토큰(액세스/리프레시) 재발급 API")
    public ResponseEntity<?> refreshTokens(HttpServletRequest request) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .headers(authService.refreshTokens(request))
            .body(ApiResponse.success(ResponseCodeEnum.SUCCESS));
    }

}
