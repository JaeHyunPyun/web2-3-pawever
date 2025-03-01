package com.pawever.server.domain.user.service;

import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.user.entity.redis.RefreshToken;
import com.pawever.server.domain.user.jwt.JwtProperties;
import com.pawever.server.domain.user.repository.redis.RefreshTokenRepository;
import jakarta.persistence.PersistenceException;
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
        try {
            RefreshToken token = RefreshToken.builder()
                .refreshToken(refreshToken)
                .name(name)
                .ttl(jwtProperties.getRefreshTokenExpirationTime()/1000)
                .build();
            refreshTokenRepository.save(token);
            log.info("리프레시 토큰 저장: 토큰 - {}, 유저명 - {}", refreshToken, name);
        } catch (PersistenceException e) {
            log.error("리프레시 토큰 저장 실패 : 토큰 - {}, 유저명 - {} 이유 - {}", refreshToken, name, e.getMessage());
            throw new CustomException(ResponseCodeEnum.DATA_PERSISTENCE_ERROR);
        }
    }

    @Transactional
    public void removeRefreshToken(String refreshToken) {
        refreshTokenRepository.findRefreshTokenByRefreshToken(refreshToken)
            .ifPresent(token -> refreshTokenRepository.delete(token));
    }
}