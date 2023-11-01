package co.kirikiri.domain.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "refreshToken")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    
    @Id
    private String refreshToken;

    @TimeToLive
    private Long expirationInSeconds;

    private String memberIdentifier;

    public RefreshToken(final String refreshToken, final Long expirationInSeconds, final String memberIdentifier) {
        this.refreshToken = refreshToken;
        this.expirationInSeconds = expirationInSeconds;
        this.memberIdentifier = memberIdentifier;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getMemberIdentifier() {
        return memberIdentifier;
    }
}
