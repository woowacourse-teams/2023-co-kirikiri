package co.kirikiri.domain.member.vo;

import co.kirikiri.exception.AuthenticationException;

public class Password {

    private final String value;

    public Password(final String value) {
        validate(value);
        this.value = value;
    }

    private void validate(final String value) {
        if (isNotValidLength(value) || isNotValidPattern(value)) {
            throw new AuthenticationException("제약 조건에 맞지 않는 비밀번호입니다.");
        }
    }

    private boolean isNotValidLength(final String value) {
        return value.length() < 8 || value.length() > 15;
    }

    private boolean isNotValidPattern(final String value) {
        final String regex = "^(?=.*[a-z])(?=.*\\d)[a-z\\d!@#\\$%\\^&\\*\\(\\)~]+$";
        return !value.matches(regex);
    }

    public int length() {
        return value.length();
    }

    public byte[] getBytes() {
        return value.getBytes();
    }
}
