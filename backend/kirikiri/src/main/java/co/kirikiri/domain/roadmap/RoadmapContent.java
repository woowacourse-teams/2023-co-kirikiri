package co.kirikiri.domain.roadmap;

import co.kirikiri.domain.BaseCreatedTimeEntity;
import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapContent extends BaseCreatedTimeEntity {

    private static final int CONTENT_MAX_LENGTH = 150;

    @Column(length = 2200)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private Roadmap roadmap;

    @Embedded
    private final RoadmapNodes nodes = new RoadmapNodes();

    public RoadmapContent(final String content) {
        validate(content);
        this.content = content;
    }

    public RoadmapContent(final Long id, final String content) {
        this.id = id;
        this.content = content;
    }

    private void validate(final String content) {
        if (content == null) {
            return;
        }
        validateContentLength(content);
    }

    private void validateContentLength(final String content) {
        if (content.length() > CONTENT_MAX_LENGTH) {
            throw new BadRequestException(String.format("로드맵 본문의 길이는 최대 %d글자 입니다.", CONTENT_MAX_LENGTH));
        }
    }

    public void addNodes(final RoadmapNodes nodes) {
        this.nodes.addAll(nodes);
        nodes.updateAllRoadmapContent(this);
    }

    public boolean isNotSameRoadmap(final Roadmap roadmap) {
        return this.roadmap == null || !this.roadmap.equals(roadmap);
    }

    public void updateRoadmap(final Roadmap roadmap) {
        if (this.roadmap == null) {
            this.roadmap = roadmap;
        }
    }

    public String getContent() {
        return content;
    }

    public RoadmapNodes getNodes() {
        return nodes;
    }
}
