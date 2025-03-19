package com.pawever.server.domain.user.jwt;


import com.pawever.server.domain.user.dto.request.AuthPreLoginRequestDto;
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
public class JwtUtil {

    private SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}")String secret) {
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
        String roleString = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
        return Role.valueOf(roleString);
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String getCodeChallenge(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("codeChallenge", String.class);
    }

    public String getCodeChallengeMethod(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("codeChallengeMethod", String.class);
    }

    public UserResponseDto getUserResponseDto(String token){
        return UserResponseDto.builder()
            .userId(getUserId(token))
            .socialLoginUuid(getSocialLoginUuid(token))
            .name(getName(token))
            .role(getRole(token))
            .build();
    }

    public AuthPreLoginRequestDto getAuthPreLoginRequestDto(String token){
        return AuthPreLoginRequestDto.builder()
            .codeChallenge(getCodeChallenge(token))
            .codeChallengeMethod(getCodeChallengeMethod(token))
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

    public String createJwt(String category, AuthPreLoginRequestDto authPreLoginRequestDto, Long expiredMs) {

        return Jwts.builder()
            .claim("category", category)        // refresh, access 토큰 구분
            .claim("codeChallenge", authPreLoginRequestDto.getCodeChallenge())
            .claim("codeChallengeMethod", authPreLoginRequestDto.getCodeChallengeMethod())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiredMs))
            .signWith(secretKey)
            .compact();
    }
}

