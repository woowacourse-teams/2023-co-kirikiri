package co.kirikiri.domain.member.vo;

import co.kirikiri.exception.AuthenticationException;
import jakarta.persistence.Column;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EncryptedPassword {

    @Column(name = "password", nullable = false)
    private String value;

    public EncryptedPassword(final Password unencryptedPassword) {
        try {
            this.value = encrypt(unencryptedPassword);
        } catch (final NoSuchAlgorithmException exception) {
            throw new AuthenticationException(exception.getMessage());
        }
    }

    private String encrypt(final Password unencryptedPassword) throws NoSuchAlgorithmException {
        final String salt = generateSalt(unencryptedPassword.length());
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(salt.getBytes());
        messageDigest.update(unencryptedPassword.getBytes());
        final byte[] hashedPassword = messageDigest.digest();
        return Base64.getEncoder().encodeToString(hashedPassword);
    }

    private String generateSalt(final int length) {
        final SecureRandom secureRandom = new SecureRandom();
        final byte[] salt = new byte[length];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EncryptedPassword that = (EncryptedPassword) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
