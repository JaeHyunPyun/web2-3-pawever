package com.pawever.server.domain.user.controller;

import com.pawever.server.domain.user.dto.request.AuthRequestDto;
import com.pawever.server.domain.user.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/tokens")
    public ResponseEntity<?> login(@RequestBody AuthRequestDto authRequestDto, HttpServletResponse response) {

        System.out.println("email : " + authRequestDto.getEmail());
        System.out.println("logitude : " + authRequestDto.getLongitude());

        return ResponseEntity.ok()
            .headers(authService.login(authRequestDto, response))
            .build();
    }
}
