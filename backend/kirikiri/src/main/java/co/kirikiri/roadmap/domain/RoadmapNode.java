package co.kirikiri.roadmap.domain;

import co.kirikiri.common.entity.BaseEntity;
import co.kirikiri.roadmap.domain.exception.RoadmapException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapNode extends BaseEntity {

    private static final int TITLE_MIN_LENGTH = 1;
    private static final int TITLE_MAX_LENGTH = 40;
    private static final int CONTENT_MIN_LENGTH = 1;
    private static final int CONTENT_MAX_LENGTH = 2000;
    @Embedded
    private final RoadmapNodeImages roadmapNodeImages = new RoadmapNodeImages();
    @Column(length = 50, nullable = false)
    private String title;
    @Column(length = 2200, nullable = false)
    private String content;

    public RoadmapNode(final String title, final String content) {
        this(null, title, content);
    }

    public RoadmapNode(final Long id, final String title, final String content) {
        validate(title, content);
        this.id = id;
        this.title = title;
        this.content = content;
    }

    private void validate(final String title, final String content) {
        validateTitleLength(title);
        validateContentLength(content);
    }

    private void validateTitleLength(final String title) {
        if (title.length() < TITLE_MIN_LENGTH || title.length() > TITLE_MAX_LENGTH) {
            throw new RoadmapException(
                    String.format("로드맵 노드의 제목의 길이는 최소 %d글자, 최대 %d글자입니다.", TITLE_MIN_LENGTH, TITLE_MAX_LENGTH));
        }
    }

    private void validateContentLength(final String content) {
        if (content.length() < CONTENT_MIN_LENGTH || content.length() > CONTENT_MAX_LENGTH) {
            throw new RoadmapException(
                    String.format("로드맵 노드의 설명의 길이는 최소 %d글자, 최대 %d글자입니다.", CONTENT_MIN_LENGTH, CONTENT_MAX_LENGTH));
        }
    }

    public void addImages(final RoadmapNodeImages roadmapNodeImages) {
        this.roadmapNodeImages.addAll(roadmapNodeImages);
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public RoadmapNodeImages getRoadmapNodeImages() {
        return roadmapNodeImages;
    }
}
