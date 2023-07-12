package co.kirikiri.domain.member.vo;

import co.kirikiri.exception.AuthenticationException;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Identifier {

    @Column(name = "identifier", length = 50, unique = true, nullable = false)
    private String value;

    public Identifier(final String value) {
        validate(value);
        this.value = value;
    }

    private void validate(final String value) {
        if (isNotValidLength(value) || isNotValidPattern(value)) {
            throw new AuthenticationException("제약 조건에 맞지 않는 아이디입니다.");
        }
    }

    private boolean isNotValidLength(final String value) {
        return value.length() < 4 || value.length() > 20;
    }

    private boolean isNotValidPattern(final String value) {
        final String regex = "^[a-z0-9]+$";
        return !value.matches(regex);
    }
}
