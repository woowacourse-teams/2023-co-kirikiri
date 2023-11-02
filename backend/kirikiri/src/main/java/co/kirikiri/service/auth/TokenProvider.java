package co.kirikiri.service.auth;

import java.time.LocalDateTime;
import java.util.Map;

public interface TokenProvider<A, R> {

    A createAccessToken(final String subject, final Map<String, Object> claims);

    R createRefreshToken(final String subject, final Map<String, Object> claims);

    boolean isValidToken(final String token);

    LocalDateTime findTokenExpiredAt(final String token);

    String findSubject(final String token);
}
