package co.kirikiri.domain.member.vo;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.exception.BadRequestException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NicknameTest {

    @ParameterizedTest
    @ValueSource(strings = {"ab", "abcdefgh", "AB", "ABCDEFGH", "~!", "~!@#$%^&"})
    void 정상적으로_닉네임을_생성한다(final String nickname) {
        //given
        //when
        //then
        assertDoesNotThrow(() -> new Nickname(nickname));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "abcdefghe"})
    void 닉네임_길이가_틀릴_경우_예외를_던진다(final String nickname) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new Nickname(nickname))
            .isInstanceOf(BadRequestException.class);
    }
}
