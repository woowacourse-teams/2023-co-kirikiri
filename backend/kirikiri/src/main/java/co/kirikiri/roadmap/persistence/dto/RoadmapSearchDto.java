package co.kirikiri.roadmap.persistence.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RoadmapSearchDto {

    private final RoadmapSearchCreatorNickname creatorName;
    private final RoadmapSearchTitle title;
    private final RoadmapSearchTagName tagName;

    public static RoadmapSearchDto create(final String creatorName, final String title, final String tagName) {
        if (creatorName != null) {
            return new RoadmapSearchDto(new RoadmapSearchCreatorNickname(creatorName), null, null);
        }
        if (title != null) {
            return new RoadmapSearchDto(null, new RoadmapSearchTitle(title), null);
        }
        if (tagName != null) {
            return new RoadmapSearchDto(null, null, new RoadmapSearchTagName(tagName));
        }
        return new RoadmapSearchDto(null, null, null);
    }
}
