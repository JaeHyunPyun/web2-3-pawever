package com.pawever.server.domain.user.service;

import com.pawever.server.domain.user.entity.redis.RefreshToken;
import com.pawever.server.domain.user.jwt.JwtProperties;
import com.pawever.server.domain.user.repository.redis.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Transactional
    public void saveRefreshToken(String refreshToken, String name) {
        RefreshToken token = RefreshToken.builder()
            .refreshToken(refreshToken)
            .name(name)
            .ttl(jwtProperties.getRefreshTokenExpirationTime())
            .build();
        refreshTokenRepository.save(token);
        log.info("Refresh token saved: token - {}, username - {}", refreshToken, name);
    }

    @Transactional
    public void removeRefreshToken(String refreshToken) {
        refreshTokenRepository.findRefreshTokenByRefreshToken(refreshToken)
            .ifPresent(token -> refreshTokenRepository.delete(token));
    }
}