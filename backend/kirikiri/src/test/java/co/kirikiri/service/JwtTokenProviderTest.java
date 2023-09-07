package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.exception.AuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private static final String secretKey = "9zrOjg1kDd2gUp6KBbElGJj5GHP5BnneDs3nXEhdztHAUjKBX7l69JXUErBovPLn7TVWV0UCfejYZyxIjIMC5KPfSvBzo9C1gJ2";
    TokenProvider tokenProvider = new JwtTokenProvider(secretKey, 1_800_000L, 86_400_000L);

    @Test
    void 정상적으로_subject와_claims를_포함한_ACCESS_TOKEN을_생성한다() {
        //given
        final String subject = "subject";
        final String role = "role";
        final Map<String, Object> claims = new HashMap<>(Map.of("test1", "test1", "test2", "test2"));

        //when
        final String accessToken = tokenProvider.createAccessToken(subject, role, claims);

        //then
        final Claims result = getClaims(accessToken);

        assertThat(result.getSubject()).isEqualTo(subject);         // subject 확인
        for (final String claimKey : claims.keySet()) {             // custom claim 확인
            final String claim = result.get(claimKey, String.class);
            assertThat(claim).isEqualTo(claims.get(claimKey));
        }

        assertDoesNotThrow(result::getExpiration);
    }

    @Test
    void 정상적으로_subject와_claims를_포함한_REFRESH_TOKEN을_생성한다() {
        //given
        final String subject = "subject";
        final String role = "role";
        final Map<String, Object> claims = new HashMap<>(Map.of("test1", "test1", "test2", "test2"));

        //when
        final String accessToken = tokenProvider.createRefreshToken(subject, role, claims);

        //then
        final Claims result = getClaims(accessToken);

        assertThat(result.getSubject()).isEqualTo(subject);         // subject 확인
        for (final String claimKey : claims.keySet()) {             // custom claim 확인
            final String claim = result.get(claimKey, String.class);
            assertThat(claim).isEqualTo(claims.get(claimKey));
        }

        assertDoesNotThrow(result::getExpiration);
    }

    @Test
    void 정상적인_토큰의_유효성을_검사한다() {
        //given
        final String subject = "subject";
        final String role = "role";
        final Map<String, Object> claims = new HashMap<>(Map.of("test1", "test1", "test2", "test2"));
        final String accessToken = tokenProvider.createAccessToken(subject, role, claims);

        //when
        final boolean result = tokenProvider.isValidToken(accessToken);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void 만료된_토큰의_유효성을_검사한다() {
        //given
        final String subject = "subject";
        final String role = "role";
        final Map<String, Object> claims = new HashMap<>(Map.of("test1", "test1", "test2", "test2"));
        final TokenProvider tokenProvider = new JwtTokenProvider(secretKey, 0L, 0L);
        final String accessToken = tokenProvider.createAccessToken(subject, role, claims);

        //when
        //then
        assertThatThrownBy(() -> tokenProvider.isValidToken(accessToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Expired Token");
    }

    @Test
    void 유효하지_않은_토큰의_유효성을_검사한다() {
        //given
        final String accessToken = "Invalid Token";

        //when
        //then
        assertThatThrownBy(() -> tokenProvider.isValidToken(accessToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid Token");
    }

    private Claims getClaims(final String accessToken) {
        return assertDoesNotThrow(() ->      // 예외가 터진다면 서명이 유효하지 않거나 만료기간 지난 토큰
                Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                        .build()
                        .parseClaimsJws(accessToken)
                        .getBody());
    }

    @Test
    void 토큰에서_Subject를_가져온다() {
        //given
        final String subject = "subject";
        final String role = "role";
        final Map<String, Object> claims = new HashMap<>(Map.of("test1", "test1", "test2", "test2"));
        final String accessToken = tokenProvider.createAccessToken(subject, role, claims);

        //when
        final String result = tokenProvider.findSubject(accessToken);

        //then
        assertThat(result).isEqualTo(subject);
    }

    @Test
    void 토큰에서_Role을_가져온다() {
        //given
        final String subject = "subject";
        final String role = "role";
        final Map<String, Object> claims = new HashMap<>(Map.of("test1", "test1", "test2", "test2"));
        final String accessToken = tokenProvider.createAccessToken(subject, role, claims);

        //when
        final String result = tokenProvider.findRole(accessToken);

        //then
        assertThat(result).isEqualTo(role);
    }
}
