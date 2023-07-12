package co.kirikiri.domain.member;

import co.kirikiri.exception.AuthenticationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

    private String password;

    public Password(final String password) {
        validate(password);
        this.password = password;
    }

    private void validate(final String password) {
        if (isNotValidLength(password) || isNotValidPattern(password)) {
            throw new AuthenticationException("제약 조건에 맞지 않는 비밀번호입니다.");
        }
    }

    private boolean isNotValidLength(final String password) {
        return password.length() < 8 || password.length() > 15;
    }

    private boolean isNotValidPattern(final String password) {
        final String regex = "^(?=.*[a-z])(?=.*\\d)[a-z\\d!@#\\$%\\^&\\*\\(\\)~]+$";
        return !password.matches(regex);
    }
}
