package com.pawever.server.domain.user.entity.redis;

import com.pawever.server.domain.user.jwt.JwtProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor
@RedisHash(value = "refreshToken")
public class RefreshToken {

    @Id
    private String refreshToken;    // 리프레시 토큰

    private String name;            // 유저 이름

    @TimeToLive
    private Long ttl;               // 리프레시 토큰 생명주기(자동 소멸)

    @Builder
    public RefreshToken(String refreshToken, String name, Long ttl) {
        this.refreshToken = refreshToken;
        this.name = name;
        this.ttl = ttl;
    }
}

