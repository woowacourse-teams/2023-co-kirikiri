package co.kirikiri.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private static final String secretKey = "abcdefghijklmnopqrstuvwzyzabcdefghijklmnopqrstuvwzyz";
    TokenProvider tokenProvider = new JwtTokenProvider(secretKey, 1_800_000L, 86_400_000L);


    @Test
    void 정상적으로_subject와_claims를_포함한_ACCESS_TOKEN을_생성한다() {
        //given
        final String subject = "subject";
        final Map<String, Object> claims = new HashMap<>(Map.of("test1", "test1", "test2", "test2"));

        //when
        final String accessToken = tokenProvider.createAccessToken(subject, claims);

        //then
        final Claims result = assertDoesNotThrow(() ->      // 예외가 터진다면 서명이 유효하지 않거나 만료기간 지난 토큰
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken).getBody());

        assertThat(result.getSubject()).isEqualTo(subject);         // subject 확인
        for (final String claimKey : claims.keySet()) {             // custom claim 확인
            final String claim = result.get(claimKey, String.class);
            assertThat(claim).isEqualTo(claims.get(claimKey));
        }

        assertDoesNotThrow(result::getExpiration);
    }

    @Test
    void 정상적으로_subject와_claims를_포함한_REFREST_TOKEN을_생성한다() {
        //given
        final String subject = "subject";
        final Map<String, Object> claims = new HashMap<>(Map.of("test1", "test1", "test2", "test2"));

        //when
        final String accessToken = tokenProvider.createRefreshToken(subject, claims);

        //then
        final Claims result = assertDoesNotThrow(() ->      // 예외가 터진다면 서명이 유효하지 않거나 만료기간 지난 토큰
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken).getBody());

        assertThat(result.getSubject()).isEqualTo(subject);         // subject 확인
        for (final String claimKey : claims.keySet()) {             // custom claim 확인
            final String claim = result.get(claimKey, String.class);
            assertThat(claim).isEqualTo(claims.get(claimKey));
        }

        assertDoesNotThrow(result::getExpiration);
    }
}
