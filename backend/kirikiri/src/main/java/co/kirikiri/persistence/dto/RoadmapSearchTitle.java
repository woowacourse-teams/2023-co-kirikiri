package co.kirikiri.persistence.dto;

import co.kirikiri.exception.BadRequestException;
import java.util.Objects;

public class RoadmapSearchTitle {

    private static final int MIN_LENGTH = 1;

    private final String value;

    public RoadmapSearchTitle(final String title) {
        final String removedBlankTitle = removeBlank(title);
        validateLength(removedBlankTitle);
        this.value = removedBlankTitle;
    }

    private String removeBlank(final String title) {
        return title.replaceAll(" ", "");
    }

    private void validateLength(final String title) {
        if (title.length() < MIN_LENGTH) {
            throw new BadRequestException(
                    String.format("검색어는 최소 %d자부터 가능합니다.", MIN_LENGTH));
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
        final RoadmapSearchTitle that = (RoadmapSearchTitle) o;
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
