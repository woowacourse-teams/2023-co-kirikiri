package co.kirikiri.domain.roadmap;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapNodes {

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "roadmapContent")
    private final List<RoadmapNode> values = new ArrayList<>();

    public RoadmapNodes(final List<RoadmapNode> roadmapNodes) {
        this.values.addAll(roadmapNodes);
    }

    public void add(final RoadmapNode roadmapNode) {
        this.values.add(roadmapNode);
    }

    public void addAll(final RoadmapNodes roadmapNodes) {
        this.values.addAll(roadmapNodes.getValues());
    }

    public void updateAllRoadmapContent(final RoadmapContent content) {
        for (final RoadmapNode value : values) {
            updateRoadmapContent(value, content);
        }
    }

    private void updateRoadmapContent(final RoadmapNode roadmapNode, final RoadmapContent content) {
        if (roadmapNode.isNotSameRoadmapContent(content)) {
            roadmapNode.updateRoadmapContent(content);
        }
    }

    public List<RoadmapNode> getValues() {
        return values;
    }
}
