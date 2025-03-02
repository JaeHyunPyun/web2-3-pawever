package com.pawever.server.domain.user.jwt;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;


@Getter
@ConfigurationProperties(prefix = "spring.jwt")
public class JwtProperties {
    private final Long accessTokenExpirationTime;    // 액세스 토큰 TTL
    private final Long refreshTokenExpirationTime;   // 리프레시 토큰 TTL

    @ConstructorBinding
    public JwtProperties(Long accessTokenExpirationTime, Long refreshTokenExpirationTime) {
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
    }
}

