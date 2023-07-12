package co.kirikiri.domain.member.vo;

import co.kirikiri.exception.AuthenticationException;

public class Nickname {

    private final String value;

    public Nickname(final String value) {
        validate(value);
        this.value = value;
    }

    private void validate(final String value) {
        if (isNotValidLength(value)) {
            throw new AuthenticationException("제약 조건에 맞지 않는 닉네임입니다.");
        }
    }

    private boolean isNotValidLength(final String value) {
        return value.length() < 2 || value.length() > 8;
    }

}
