package co.kirikiri.domain.auth.vo;

import co.kirikiri.exception.ServerException;
import jakarta.persistence.Column;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EncryptedToken {

    private static final String ALGORITHM = "SHA-256";

    @Column(name = "token", nullable = false)
    private String value;


    public EncryptedToken(final String rawToken) {
        try {
            this.value = encrypt(rawToken);
        } catch (final NoSuchAlgorithmException exception) {
            throw new ServerException(exception.getMessage());
        }
    }

    private String encrypt(final String rawToken) throws NoSuchAlgorithmException {
        final MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
        messageDigest.update(rawToken.getBytes());
        final byte[] hashedToken = messageDigest.digest();
        return Base64.getEncoder().encodeToString(hashedToken);
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
