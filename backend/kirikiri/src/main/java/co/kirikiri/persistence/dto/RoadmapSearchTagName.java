package co.kirikiri.persistence.dto;

import co.kirikiri.exception.BadRequestException;

public record RoadmapSearchTagName(
        String value
) {

    private static final int MIN_LENGTH = 1;

    public RoadmapSearchTagName(final String value) {
        final String removedBlankName = removeBlank(value);
        validateLength(removedBlankName);
        this.value = removedBlankName;
    }

    private String removeBlank(final String name) {
        return name.replaceAll(" ", "");
    }

    private void validateLength(final String name) {
        if (name.length() < MIN_LENGTH) {
            throw new BadRequestException(
                    String.format("검색어는 최소 %d자부터 가능합니다.", MIN_LENGTH));
        }
    }
}
