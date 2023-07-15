package co.kirikiri.domain.roadmap;

import co.kirikiri.exception.BadRequestException;
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

    private static final int ROADMAP_NODES_NIN_SIZE = 1;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "roadmapContent")
    private List<RoadmapNode> roadmapNodes = new ArrayList<>();

    public RoadmapNodes(final List<RoadmapNode> roadmapNodes) {
        validate(roadmapNodes);
        this.roadmapNodes.addAll(roadmapNodes);
    }

    private void validate(final List<RoadmapNode> roadmapNodes) {
        if (roadmapNodes.size() < ROADMAP_NODES_NIN_SIZE) {
            throw new BadRequestException("로드맵의 노드는 최소" + ROADMAP_NODES_NIN_SIZE + "1개 이상 존재해야 합니다.");
        }
    }

    public void add(final RoadmapNode roadmapNode) {
        this.roadmapNodes.add(roadmapNode);
    }

    public List<RoadmapNode> getRoadmapNodes() {
        return roadmapNodes;
    }
}
