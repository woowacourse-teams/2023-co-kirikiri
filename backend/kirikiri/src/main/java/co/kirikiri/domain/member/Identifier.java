package co.kirikiri.domain.member;

import co.kirikiri.exception.AuthenticationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Identifier {

    private String Identifier;

    public Identifier(final String identifier) {
        validate(identifier);
        Identifier = identifier;
    }

    private void validate(final String identifier) {
        if (isNotValidLength(identifier) || isNotValidPattern(identifier)) {
            throw new AuthenticationException("제약 조건에 맞지 않는 아이디입니다.");
        }
    }

    private boolean isNotValidLength(final String identifier) {
        return identifier.length() < 4 || identifier.length() > 20;
    }

    private boolean isNotValidPattern(final String identifier) {
        final String regex = "^[a-z0-9]+$";
        return !identifier.matches(regex);
    }
}
