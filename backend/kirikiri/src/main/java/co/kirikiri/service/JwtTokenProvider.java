package co.kirikiri.service;

import co.kirikiri.exception.AuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider implements TokenProvider {

    private final String secretKey;
    private final Long accessTokenValidityInSeconds;
    private final Long refreshTokenValidityInSeconds;

    public JwtTokenProvider(@Value("${jwt.secret-key}") final String secretKey,
                            @Value("#{T(Long).parseLong('${jwt.access-token-validity-in-seconds}')}") final Long accessTokenValidityInSeconds,
                            @Value("#{T(Long).parseLong('${jwt.refresh-token-validity-in-seconds}')}") final Long refreshTokenValidityInSeconds) {
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

    private String createToken(final Long tokenValidityInSeconds, final String subject, final Map<String, Object> claims) {
        final SecretKey signingKey = createKey();
        final Date expiration = createExpiration(tokenValidityInSeconds);
        return Jwts.builder()
                .signWith(signingKey)
                .setClaims(claims)
                .setSubject(subject)
                .setExpiration(expiration)
                .compact();
    }

    private SecretKey createKey() {
        final byte[] secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(secretKeyBytes);
    }

    private Date createExpiration(final Long validity) {
        final long now = new Date().getTime();
        return new Date(now + validity);
    }

    @Override
    public boolean validateToken(final String token) {
        try {
            parseToClaimsJws(token);
        } catch (final ExpiredJwtException expiredJwtException) {
            throw new AuthenticationException("Expired Token");
        } catch (final JwtException jwtException) {
            throw new AuthenticationException("Invalid Token");
        }
        return true;
    }

    private Jws<Claims> parseToClaimsJws(final String token) {
        final SecretKey signingKey = createKey();
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
    }

    @Override
    public LocalDateTime findTokenExpiredAt(final String token) {
        final Jws<Claims> claimsJws = parseToClaimsJws(token);
        final Date expiration = claimsJws.getBody()
                .getExpiration();
        return expiration.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
