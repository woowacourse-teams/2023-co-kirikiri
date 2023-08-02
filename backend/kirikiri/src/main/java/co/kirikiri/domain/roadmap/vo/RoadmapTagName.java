package co.kirikiri.domain.roadmap.vo;

import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.Column;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapTagName {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 10;

    @Column(name = "name", length = 15)
    private String value;

    public RoadmapTagName(final String value) {
        final String removedSpaceValue = value.replaceAll(" ", "");
        validate(removedSpaceValue);
        this.value = removedSpaceValue;
    }

    private void validate(final String name) {
        if (name.length() < MIN_LENGTH || name.length() > MAX_LENGTH) {
            throw new BadRequestException(
                    String.format("태그 이름은 최소 %d자부터 최대 %d자까지 가능합니다.", MIN_LENGTH, MAX_LENGTH));
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RoadmapTagName that = (RoadmapTagName) o;
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
