package com.pawever.server.domain.user.jwt;


import com.pawever.server.domain.user.dto.response.UserResponseDto;
import com.pawever.server.domain.user.enums.Role;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public Long getUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", Long.class);
    }

    public String getSocialLoginUuid(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("socialLoginUuid", String.class);
    }

    public String getName(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("name", String.class);
    }

    public Role getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", Role.class);
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public UserResponseDto getUserResponseDto(String token){
        return UserResponseDto.builder()
            .userId(getUserId(token))
            .socialLoginUuid(getSocialLoginUuid(token))
            .name(getName(token))
            .role(getRole(token))
            .build();
    }

    public String createJwt(String category, UserResponseDto userResponseDto, Long expiredMs) {

        return Jwts.builder()
            .claim("category", category)        // refresh, access 토큰 구분
            .claim("userId", userResponseDto.getUserId())
            .claim("socialLoginUuid", userResponseDto.getSocialLoginUuid())
            .claim("name", userResponseDto.getName())
            .claim("role", userResponseDto.getRole())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiredMs))
            .signWith(secretKey)
            .compact();
    }
}

