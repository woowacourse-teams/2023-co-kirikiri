package co.kirikiri.persistence.dto;

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
        if (title == null && tagName == null) {
            return new RoadmapSearchDto(new RoadmapSearchCreatorNickname(creatorName), null, null);
        }
        if (title == null) {
            return new RoadmapSearchDto(new RoadmapSearchCreatorNickname(creatorName), null,
                    new RoadmapSearchTagName(tagName));
        }
        if (tagName == null) {
            return new RoadmapSearchDto(new RoadmapSearchCreatorNickname(creatorName), new RoadmapSearchTitle(title), null);
        }
        return new RoadmapSearchDto(new RoadmapSearchCreatorNickname(creatorName), new RoadmapSearchTitle(title),
                new RoadmapSearchTagName(tagName));
    }
}
