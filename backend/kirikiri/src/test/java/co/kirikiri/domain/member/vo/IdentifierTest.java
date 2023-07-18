package co.kirikiri.domain.member.vo;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.exception.BadRequestException;
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
    void 아이디_길이가_4미만_20초과일_경우_예외를_던진다(final String identifier) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new Identifier(identifier))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Abcd", "ABCd", "abcdefG", "abcDefgHi", "abcdefghijklmnopqrsT"})
    void 아이디에_대문자가_있을_경우(final String identifier) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new Identifier(identifier))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc~", "~!@#$%^&*()", "abcdef!"})
    void 아이디에_특수문자가_있을_경우(final String identifier) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new Identifier(identifier))
                .isInstanceOf(BadRequestException.class);
    }
}
