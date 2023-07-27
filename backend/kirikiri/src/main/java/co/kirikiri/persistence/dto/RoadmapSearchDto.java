package co.kirikiri.persistence.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RoadmapSearchDto {

    private final RoadmapSearchTitle title;
    private final Long creatorId;
    private final RoadmapSearchTagName tagName;

    public static RoadmapSearchDto create(final String title, final Long creatorId, final String tagName) {
        if (title == null && tagName == null) {
            return new RoadmapSearchDto(null, creatorId, null);
        }
        if (title == null) {
            return new RoadmapSearchDto(null, creatorId, new RoadmapSearchTagName(tagName));
        }
        if (tagName == null) {
            return new RoadmapSearchDto(new RoadmapSearchTitle(title), creatorId, null);
        }
        return new RoadmapSearchDto(new RoadmapSearchTitle(title), creatorId, new RoadmapSearchTagName(tagName));
    }
}
