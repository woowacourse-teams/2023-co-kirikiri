package co.kirikiri.domain.roadmap;

import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapNode {

    private static final int TITLE_MIN_LENGTH = 1;
    private static final int TITLE_MAX_LENGTH = 40;
    private static final int CONTENT_MIN_LENGTH = 1;
    private static final int CONTENT_MAX_LENGTH = 200;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(length = 2200, nullable = false)
    private String content;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_node_id")
    private final List<RoadmapNodeImage> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_content_id", nullable = false)
    private RoadmapContent roadmapContent;

    public RoadmapNode(final String title, final String content) {
        validate(title, content);
        this.title = title;
        this.content = content;
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
            throw new BadRequestException(
                    String.format("로드맵 노드의 제목의 길이는 최소 %d글자, 최대 %d글자입니다.", TITLE_MIN_LENGTH, TITLE_MAX_LENGTH));
        }
    }

    private void validateContentLength(final String content) {
        if (content.length() < CONTENT_MIN_LENGTH || content.length() > CONTENT_MAX_LENGTH) {
            throw new BadRequestException(
                    String.format("로드맵 노드의 설명의 길이는 최소 %d글자, 최대 %d글자입니다.", CONTENT_MIN_LENGTH, CONTENT_MAX_LENGTH));
        }
    }

    public boolean isNotSameRoadmapContent(final RoadmapContent roadmapContent) {
        return this.roadmapContent == null || !this.roadmapContent.equals(roadmapContent);
    }

    public void updateRoadmapContent(final RoadmapContent roadmapContent) {
        if (this.roadmapContent == null) {
            this.roadmapContent = roadmapContent;
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public RoadmapContent getRoadmapContent() {
        return roadmapContent;
    }

    public List<RoadmapNodeImage> getImages() {
        return images;
    }
}
