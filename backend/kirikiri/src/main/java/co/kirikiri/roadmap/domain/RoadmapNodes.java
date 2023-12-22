package co.kirikiri.roadmap.domain;

import co.kirikiri.roadmap.domain.exception.RoadmapException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapNodes {

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            mappedBy = "roadmapContent")
    private final List<RoadmapNode> values = new ArrayList<>();

    public RoadmapNodes(final List<RoadmapNode> roadmapNodes) {
        validateTitleDistinct(roadmapNodes);
        this.values.addAll(new ArrayList<>(roadmapNodes));
    }

    private void validateTitleDistinct(final List<RoadmapNode> roadmapNodes) {
        final int distinctNameCount = roadmapNodes.stream()
                .map(RoadmapNode::getTitle)
                .collect(Collectors.toSet())
                .size();
        if (roadmapNodes.size() != distinctNameCount) {
            throw new RoadmapException("한 로드맵에 같은 이름의 노드가 존재할 수 없습니다.");
        }
    }

    public void add(final RoadmapNode roadmapNode) {
        this.values.add(roadmapNode);
        validateTitleDistinct(values);
    }

    public void addAll(final RoadmapNodes roadmapNodes) {
        this.values.addAll(new ArrayList<>(roadmapNodes.values));
        validateTitleDistinct(values);
    }

    public void updateAllRoadmapContent(final RoadmapContent content) {
        for (final RoadmapNode roadmapNode : values) {
            updateRoadmapContent(roadmapNode, content);
        }
    }

    private void updateRoadmapContent(final RoadmapNode roadmapNode, final RoadmapContent content) {
        if (roadmapNode.isNotSameRoadmapContent(content)) {
            roadmapNode.updateRoadmapContent(content);
        }
    }

    public Optional<RoadmapNode> findById(final Long roadmapNodeId) {
        return values.stream()
                .filter(it -> it.getId().equals(roadmapNodeId))
                .findAny();
    }

    public Optional<RoadmapNode> findByTitle(final String title) {
        return values.stream()
                .filter(it -> it.getTitle().equals(title))
                .findAny();
    }

    public int size() {
        return values.size();
    }

    public List<RoadmapNode> getValues() {
        return new ArrayList<>(values);
    }
}
