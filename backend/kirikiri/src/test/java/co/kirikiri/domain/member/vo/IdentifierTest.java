package co.kirikiri.domain.member.vo;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.exception.AuthenticationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IdentifierTest {

    @ParameterizedTest
    @ValueSource(strings = {"ab12", "abcdefghijklmnopqrst"})
    void 정상적으로_아이디를_생성한다(final String identifier) {
        //given
        //when
        //then
        assertDoesNotThrow(() -> new Identifier(identifier));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "abcdefghijklmnopqrst1"})
    void 아이디_길이가_틀릴_경우_예외를_던진다(final String identifier) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new Identifier(identifier))
            .isInstanceOf(AuthenticationException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc!", "Abcd", "!@#$%^", "123!", "ab cd"})
    void 아이디가_허용되지_않는_문자인_경우_예외를_던진다(final String identifier) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new Identifier(identifier))
            .isInstanceOf(AuthenticationException.class);
    }
}
