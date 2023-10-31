package co.kirikiri.domain.auth;

import co.kirikiri.domain.member.Member;
import co.kirikiri.exception.ServerException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToken")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    private static final String ALGORITHM = "SHA-256";

    @Id
    private String refreshToken;

    private LocalDateTime expiredAt;

    private Member member;

    public RefreshToken(final String rawToken) {
        this.refreshToken = encrypt(rawToken);
    }

    public RefreshToken(final String rawToken, final LocalDateTime expiredAt, final Member member) {
        this.refreshToken = encrypt(rawToken);
        this.expiredAt = expiredAt;
        this.member = member;
    }

    private String encrypt(final String rawToken) {
        final MessageDigest messageDigest = findMessageDigest();
        messageDigest.update(rawToken.getBytes());
        final byte[] hashedToken = messageDigest.digest();
        return Base64.getEncoder().encodeToString(hashedToken);
    }

    private MessageDigest findMessageDigest() {
        try {
            return MessageDigest.getInstance(ALGORITHM);
        } catch (final NoSuchAlgorithmException exception) {
            throw new ServerException(exception.getMessage());
        }
    }

    public boolean isExpired() {
        return expiredAt.isBefore(LocalDateTime.now());
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Member getMember() {
        return member;
    }
}
