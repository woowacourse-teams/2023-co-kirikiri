package co.kirikiri.domain.member.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EncryptedPasswordTest {

    @Test
    void 비밀번호를_정상적으로_암호화한다() {
        //given
        final Password password1 = new Password("password1!");
        final Password password2 = new Password("password1");

        //when
        final EncryptedPassword encryptedPassword1 = new EncryptedPassword(password1);
        final EncryptedPassword encryptedPassword2 = new EncryptedPassword(password2);

        //then
        assertThat(encryptedPassword1).isNotEqualTo(encryptedPassword2);
    }
}
