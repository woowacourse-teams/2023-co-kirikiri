package co.kirikiri.service.auth;

import java.util.Map;

public interface TokenProvider {

    String createAccessToken(final String subject, final Map<String, Object> claims);

    String createRefreshToken(final String subject, final Map<String, Object> claims);

    boolean validateToken(final String token);
}
