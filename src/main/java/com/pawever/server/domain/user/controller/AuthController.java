package com.pawever.server.domain.user.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.user.dto.request.AuthRequestDto;
import com.pawever.server.domain.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/tokens")
    @Operation(summary = "로그인 및 액세스 토큰 발급")
    public ResponseEntity<ApiResponse> login(@RequestBody AuthRequestDto authRequestDto) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .headers(authService.login(authRequestDto))
            .body(ApiResponse.success(ResponseCodeEnum.SUCCESS));
    }

    @PostMapping("/refreshedtokens")
    @Operation(summary = "리프레시 토큰으로 액세스 토큰 발급")
    public ResponseEntity<?> refreshTokens(HttpServletRequest request) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .headers(authService.refreshTokens(request))
            .body(ApiResponse.success(ResponseCodeEnum.SUCCESS));
    }

}
