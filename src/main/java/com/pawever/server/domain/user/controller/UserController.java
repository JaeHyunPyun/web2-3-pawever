package com.pawever.server.domain.user.controller;

import com.pawever.server.domain.user.enums.Role;
import com.pawever.server.domain.user.jwt.JwtUtil;
import com.pawever.server.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final JwtUtil jwtUtil;
    private final UserService userService;


    @GetMapping("/admin")
    public String admin(HttpServletRequest request) {
        // admin 권한 확인용 테스트 컨트롤러입니다.

        String accessTokenWithBearer= request.getHeader("Authorization");
        log.info("accessTokenWithBearer: " + accessTokenWithBearer);

        String accessToken = accessTokenWithBearer.split(" ")[1];
        String category = jwtUtil.getCategory(accessToken);
        String socialLoginUuid = jwtUtil.getSocialLoginUuid(accessToken);
        Role role = jwtUtil.getRole(accessToken);

        log.info("category: "+category);
        log.info("socialLoginUuid: "+socialLoginUuid);
        log.info("role: "+role.name());

        return "admin authority confirmed";
    }

    @DeleteMapping("/profiles")
    public ResponseEntity<?> withdraw(HttpServletRequest request, HttpServletResponse response){

        // 1. 로그아웃(서버 Refresh토큰 삭제 + 쿠키 초기화)
        userService.logout(request, response);

        // 2. 회원정보 삭제
        userService.softDeleteUserByUuid(request);

        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }

}
