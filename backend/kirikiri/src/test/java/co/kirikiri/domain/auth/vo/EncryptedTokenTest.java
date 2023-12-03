package co.kirikiri.domain.auth.vo;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.auth.EncryptedToken;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EncryptedTokenTest {

    @ParameterizedTest
    @ValueSource(strings = {"a", "abc", "", "token", "abcdefghijklmnopqrstuvwxyz"})
    void 정상적으로_토큰을_암호화한다(final String value) {
        //given
        //when
        final EncryptedToken encryptedToken = new EncryptedToken(value);

        //then
        assertThat(encryptedToken.getValue()).isNotEqualTo(value);
    }
}
