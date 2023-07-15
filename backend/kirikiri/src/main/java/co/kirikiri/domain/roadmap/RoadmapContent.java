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
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private RoadmapNodes nodes;

    public RoadmapContent(final String content, final RoadmapNodes nodes) {
        validate(content);
        this.content = content;
        this.nodes = new RoadmapNodes();
        updateRoadmapNodes(nodes);
    }

    public void validate(final String content) {
        if (Objects.isNull(content)) {
            return;
        }
        if (content.length() > CONTENT_MAX_LENGTH) {
            throw new BadRequestException("로드맵 본문의 길이는 최대 " + CONTENT_MAX_LENGTH + "글자 입니다.");
        }
    }

    private void updateRoadmapNodes(final RoadmapNodes nodes) {
        for (final RoadmapNode node : nodes.getRoadmapNodes()) {
            this.nodes.add(node);
            node.setRoadmapContent(this);
        }
    }

    public void setRoadmap(final Roadmap roadmap) {
        this.roadmap = roadmap;
    }
}
