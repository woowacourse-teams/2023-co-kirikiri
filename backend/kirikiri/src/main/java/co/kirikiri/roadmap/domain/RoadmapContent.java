package co.kirikiri.roadmap.domain;

import co.kirikiri.common.entity.BaseUpdatedTimeEntity;
import co.kirikiri.roadmap.domain.exception.RoadmapException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Optional;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RoadmapContent extends BaseUpdatedTimeEntity {

    private static final int CONTENT_MAX_LENGTH = 2000;

    @Column(length = 2200)
    private String content;

    private Long roadmapId;

    @Embedded
    private RoadmapNodes nodes;

    public RoadmapContent(final String content, final Long roadmapId, final RoadmapNodes nodes) {
        this(null, content, roadmapId, nodes);
    }

    public RoadmapContent(final Long id, final String content, final Long roadmapId, final RoadmapNodes nodes) {
        validate(content);
        this.id = id;
        this.content = content;
        this.roadmapId = roadmapId;
        this.nodes = nodes;
    }

    private void validate(final String content) {
        if (content == null) {
            return;
        }
        validateContentLength(content);
    }

    private void validateContentLength(final String content) {
        if (content.length() > CONTENT_MAX_LENGTH) {
            throw new RoadmapException(String.format("로드맵 본문의 길이는 최대 %d글자입니다.", CONTENT_MAX_LENGTH));
        }
    }

    public int nodesSize() {
        return nodes.size();
    }

    public Optional<RoadmapNode> findRoadmapNodeById(final Long id) {
        return nodes.findById(id);
    }

    public Optional<RoadmapNode> findRoadmapNodeByTitle(final String title) {
        return nodes.findByTitle(title);
    }

    public String getContent() {
        return content;
    }

    public Long getRoadmapId() {
        return roadmapId;
    }

    public RoadmapNodes getNodes() {
        return nodes;
    }
}
