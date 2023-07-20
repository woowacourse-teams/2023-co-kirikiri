package co.kirikiri.domain.roadmap;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapNodes {

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "roadmapContent")
    private final List<RoadmapNode> values = new ArrayList<>();

    public RoadmapNodes(final List<RoadmapNode> values) {
        this.values.addAll(values);
    }

    public void add(final RoadmapNode roadmapNode) {
        this.values.add(roadmapNode);
    }

    public void addAll(final RoadmapNodes roadmapNodes) {
        this.values.addAll(roadmapNodes.getValues());
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
                .filter(it -> it.getId() != null)
                .filter(it -> it.getId().equals(roadmapNodeId))
                .findAny();
    }

    public int size() {
        return values.size();
    }

    public List<RoadmapNode> getValues() {
        return values;
    }
}
