package co.kirikiri.domain.roadmap;

import co.kirikiri.domain.BaseTimeEntity;
import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class RoadmapContent extends BaseTimeEntity {

    private static final int CONTENT_MAX_LENGTH = 150;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    private void validate(final String content) {
        if (content == null) {
            return;
        }
        validateContentLength(content);
    }

    private void validateContentLength(final String content) {
        if (content.length() > CONTENT_MAX_LENGTH) {
            throw new BadRequestException("로드맵 본문의 길이는 최대 " + CONTENT_MAX_LENGTH + "글자 입니다.");
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

    public RoadmapNodes getNodes() {
        return nodes;
    }
}
