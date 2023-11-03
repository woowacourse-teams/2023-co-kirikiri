package co.kirikiri.persistence.auth;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final Long refreshTokenValidityInSeconds;

    public RefreshTokenRepository(final RedisTemplate<String, String> redisTemplate,
                                  @Value("${jwt.refresh-token-validity-in-seconds}") final Long refreshTokenValidityInSeconds) {
        this.redisTemplate = redisTemplate;
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
    }

    public void save(final String refreshToken, final String memberIdentifier) {
        redisTemplate.opsForValue()
                .set(refreshToken, memberIdentifier, refreshTokenValidityInSeconds / 1000, TimeUnit.SECONDS);
    }

    public Optional<String> findMemberIdentifierByRefreshToken(final String refreshToken) {
        final String memberIdentifier = redisTemplate.opsForValue().get(refreshToken);
        if (memberIdentifier == null) {
            return Optional.empty();
        }
        return Optional.of(memberIdentifier);
    }
}
