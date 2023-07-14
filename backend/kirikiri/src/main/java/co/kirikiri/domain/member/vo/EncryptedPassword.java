package co.kirikiri.domain.member.vo;

import co.kirikiri.exception.ServerException;
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

    private static final String ALGORITHM = "SHA-256";

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String salt;

    public EncryptedPassword(final Password unencryptedPassword) {
        try {
            this.salt = generateSalt(unencryptedPassword.length());
            this.password = encrypt(unencryptedPassword, salt);
        } catch (final NoSuchAlgorithmException exception) {
            throw new ServerException(exception.getMessage());
        }
    }

    private String generateSalt(final int length) {
        final SecureRandom secureRandom = new SecureRandom();
        final byte[] value = new byte[length];
        secureRandom.nextBytes(value);
        return Base64.getEncoder().encodeToString(value);
    }

    private String encrypt(final Password unencryptedPassword, final String salt) throws NoSuchAlgorithmException {
        final MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
        messageDigest.update(salt.getBytes());
        messageDigest.update(unencryptedPassword.getBytes());
        final byte[] hashedPassword = messageDigest.digest();
        return Base64.getEncoder().encodeToString(hashedPassword);
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
        return Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(password);
    }
}
