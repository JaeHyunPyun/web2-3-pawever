package com.pawever.server.domain.user.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.user.dto.request.AuthRequestDto;
import com.pawever.server.domain.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<?> login(@RequestBody AuthRequestDto authRequestDto) {
        return ResponseEntity.status(ResponseCodeEnum.NO_CONTENT.getStatus())
            .headers(authService.login(authRequestDto))
            .build();
    }

    @PostMapping("/refreshedtokens")
    public ResponseEntity<?> refreshTokens(HttpServletRequest request) {
        return ResponseEntity.status(ResponseCodeEnum.NO_CONTENT.getStatus())
            .headers(authService.refreshTokens(request))
            .build();
    }

}
