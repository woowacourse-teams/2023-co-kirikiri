package co.kirikiri.service;

import java.time.LocalDateTime;
import java.util.Map;

public interface TokenProvider {

    String createAccessToken(final String subject, final String role, final Map<String, Object> claims);

    String createRefreshToken(final String subject, final String role, final Map<String, Object> claims);

    boolean isValidToken(final String token);

    LocalDateTime findTokenExpiredAt(final String token);

    String findSubject(final String token);

    String findRole(final String token);
}
