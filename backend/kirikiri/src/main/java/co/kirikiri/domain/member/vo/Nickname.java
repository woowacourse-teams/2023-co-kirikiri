package co.kirikiri.domain.member.vo;

import co.kirikiri.exception.AuthenticationException;
import jakarta.persistence.Column;
import java.util.Objects;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Nickname {

    @Column(name = "nickname", length = 15, nullable = false)
    private String value;

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Nickname nickname = (Nickname) o;
        return Objects.equals(value, nickname.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
