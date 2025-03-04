package com.pawever.server.domain.user.repository.redis;

import com.pawever.server.domain.user.entity.redis.RefreshToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findRefreshTokenByRefreshToken(String refreshToken);
}

