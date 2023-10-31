package co.kirikiri.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RefreshTokenTest {

    @ParameterizedTest
    @ValueSource(strings = {"a", "abc", "", "token", "abcdefghijklmnopqrstuvwxyz"})
    void 정상적으로_토큰을_암호화한다(final String value) {
        //given
        //when
        final RefreshToken encryptedToken = new RefreshToken(value);

        //then
        assertThat(encryptedToken.getRefreshToken()).isNotEqualTo(value);
    }
}
