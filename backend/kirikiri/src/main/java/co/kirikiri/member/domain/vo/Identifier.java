package co.kirikiri.member.domain.vo;

import co.kirikiri.member.domain.exception.MemberException;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Identifier {

    private static final int MIN_LENGTH = 4;
    private static final int MAX_LENGTH = 40;

    @Column(name = "identifier", length = 50, unique = true, nullable = false)
    private String value;

    public Identifier(final String value) {
        validate(value);
        this.value = value;
    }

    private void validate(final String value) {
        if (isNotValidLength(value)) {
            throw new MemberException("제약 조건에 맞지 않는 아이디입니다.");
        }
    }

    private boolean isNotValidLength(final String value) {
        return value.length() < MIN_LENGTH || value.length() > MAX_LENGTH;
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
