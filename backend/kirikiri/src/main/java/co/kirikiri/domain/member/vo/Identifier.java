package co.kirikiri.domain.member.vo;

import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.Column;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Identifier {

    private static final int MIN_LENGTH = 4;
    private static final int MAX_LENGTH = 20;
    private static final String REGEX = "^[a-z0-9]+$";

    @Column(name = "identifier", length = 50, unique = true, nullable = false)
    private String value;

    public Identifier(final String value) {
        validate(value);
        this.value = value;
    }

    private void validate(final String value) {
        if (isNotValidLength(value) || isNotValidPattern(value)) {
            throw new BadRequestException("제약 조건에 맞지 않는 아이디입니다.");
        }
    }

    private boolean isNotValidLength(final String value) {
        return value.length() < MIN_LENGTH || value.length() > MAX_LENGTH;
    }

    private boolean isNotValidPattern(final String value) {
        return !value.matches(REGEX);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Identifier that = (Identifier) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public String getValue() {
        return value;
    }
}
