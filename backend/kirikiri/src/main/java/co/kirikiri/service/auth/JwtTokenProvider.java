package co.kirikiri.service.auth;

import co.kirikiri.exception.AuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider implements TokenProvider {

    private final String secretKey;
    private final Long accessTokenValidityInSeconds;
    private final Long refreshTokenValidityInSeconds;

    public JwtTokenProvider(@Value("jwt.secret-key") final String secretKey,
        @Value("jwt.access-token-validity-in-seconds") final Long accessTokenValidityInSeconds,
        @Value("jwt.refresh-token-validity-in-seconds") final Long refreshTokenValidityInSeconds) {
        this.secretKey = secretKey;
        this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
    }

    @Override
    public String createAccessToken(final String subject, final Map<String, Object> claims) {
        return createToken(accessTokenValidityInSeconds, subject, claims);
    }

    @Override
    public String createRefreshToken(final String subject, final Map<String, Object> claims) {
        return createToken(refreshTokenValidityInSeconds, subject, claims);
    }

    private String createToken(final Long accessTokenValidityInSeconds, final String subject,
        final Map<String, Object> claims) {
        final SecretKey signingKey = createKey();
        final Date expiration = createExpiration(accessTokenValidityInSeconds);
        return Jwts.builder()
            .signWith(signingKey)
            .setClaims(claims)
            .setSubject(subject)
            .setExpiration(expiration)
            .compact();
    }

    private SecretKey createKey() {
        final byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Date createExpiration(final Long validity) {
        final long now = new Date().getTime();
        return new Date(now + validity);
    }

    @Override
    public boolean validateToken(final String token) {
        final SecretKey signingKey = createKey();
        try {
            checkTokenValid(token, signingKey);
        } catch (final ExpiredJwtException expiredJwtException) {
            throw new AuthenticationException("Expired Token");
        } catch (final JwtException jwtException) {
            throw new AuthenticationException("Invalid Token");
        }
        return true;
    }

    private void checkTokenValid(final String token, final SecretKey signingKey) {
        Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token);
    }
}
