package com.pawever.server.domain.user.controller;

import com.pawever.server.domain.user.enums.Role;
import com.pawever.server.domain.user.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {

    @Autowired
    JwtUtil jwtUtil;

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
}
