package co.kirikiri.persistence.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RoadmapSearchDto {

    private final Long creatorId;
    private final RoadmapSearchTitle title;
    private final RoadmapSearchTagName tagName;

    public static RoadmapSearchDto create(final Long creatorId, final String title, final String tagName) {
        if (title == null && tagName == null) {
            return new RoadmapSearchDto(creatorId, null, null);
        }
        if (title == null) {
            return new RoadmapSearchDto(creatorId, null, new RoadmapSearchTagName(tagName));
        }
        if (tagName == null) {
            return new RoadmapSearchDto(creatorId, new RoadmapSearchTitle(title), null);
        }
        return new RoadmapSearchDto(creatorId, new RoadmapSearchTitle(title), new RoadmapSearchTagName(tagName));
    }
}
