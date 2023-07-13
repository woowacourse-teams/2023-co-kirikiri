package co.kirikiri.domain.member.vo;

import co.kirikiri.exception.AuthenticationException;

public class Password {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 15;
    private static final String REGEX = "^(?=.*[a-z])(?=.*\\d)[a-z\\d!@#\\$%\\^&\\*\\(\\)~]+$";

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
        return value.length() < MIN_LENGTH || value.length() > MAX_LENGTH;
    }

    private boolean isNotValidPattern(final String value) {
        return !value.matches(REGEX);
    }

    public int length() {
        return value.length();
    }

    public byte[] getBytes() {
        return value.getBytes();
    }
}
