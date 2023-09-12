package co.kirikiri.domain.auth;

import co.kirikiri.exception.ServerException;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EncryptedToken {

    private static final String ALGORITHM = "SHA-256";

    @Column(name = "token", nullable = false)
    private String value;

    public EncryptedToken(final String rawToken) {
        this.value = encrypt(rawToken);
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EncryptedToken that = (EncryptedToken) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
