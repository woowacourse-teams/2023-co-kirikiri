package co.kirikiri.domain.member.vo;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.exception.BadRequestException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PasswordTest {

    @ParameterizedTest
    @ValueSource(strings = {"abcdefg1", "a1!@#$%^&*()~", "password12!@#$%"})
    void 정상적으로_비밀번호를_생성한다(final String password) {
        //given
        //when
        //then
        assertDoesNotThrow(() -> new Password(password));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdef1", "abcdefghijklmn12", "", " "})
    void 비밀번호_길이가_틀릴_경우_예외를_던진다(final String password) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new Password(password))
            .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Abcde1!", "abcdefghijklm1@₩"})
    void 비밀번호가_허용되지_않는_문자인_경우_예외를_던진다(final String password) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new Password(password))
            .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Abcdef!", "Abcdefghijklm1@"})
    void 비밀번호에_대문자가_들어올_경우_예외를_던진다(final String password) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new Password(password))
            .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Abcdef₩", "bcdefghijklm1♂"})
    void 비밀번호에_허용되지_않은_특수문자가_들어올_경우_예외를_던진다(final String password) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new Password(password))
            .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdefg", "sdfdsfs♂"})
    void 비밀번호에_영소문자만_들어올_경우_예외를_던진다(final String password) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new Password(password))
            .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678", "1234♂"})
    void 비밀번호에_숫자만_들어올_경우_예외를_던진다(final String password) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new Password(password))
            .isInstanceOf(BadRequestException.class);
    }
}
