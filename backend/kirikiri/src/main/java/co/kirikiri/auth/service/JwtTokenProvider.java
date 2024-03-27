package co.kirikiri.auth.service;

import co.kirikiri.common.exception.AuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtTokenProvider implements TokenProvider {

    private static final String TYPE_CLAIM_KEY = "type";
    private static final String UUID_CLAIM_KEY = "UUID";

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
        final Map<String, Object> copiedClaims = new HashMap<>(claims);
        copiedClaims.put(TYPE_CLAIM_KEY, "Access");
        copiedClaims.put(UUID_CLAIM_KEY, generateUUID());
        return createToken(accessTokenValidityInSeconds, subject, copiedClaims);
    }

    @Override
    public String createRefreshToken(final String subject, final Map<String, Object> claims) {
        final Map<String, Object> copiedClaims = new HashMap<>(claims);
        copiedClaims.put(TYPE_CLAIM_KEY, "Refresh");
        copiedClaims.put(UUID_CLAIM_KEY, generateUUID());
        return createToken(refreshTokenValidityInSeconds, subject, copiedClaims);
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private String createToken(final Long tokenValidityInSeconds, final String subject,
                               final Map<String, Object> claims) {
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
    public boolean isValidToken(final String token) {
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

    @Override
    public String findSubject(final String token) {
        final Jws<Claims> claimsJws = parseToClaimsJws(token);
        return claimsJws.getBody()
                .getSubject();
    }
}
