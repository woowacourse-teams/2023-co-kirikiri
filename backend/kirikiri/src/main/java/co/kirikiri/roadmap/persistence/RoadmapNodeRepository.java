package co.kirikiri.roadmap.persistence;

import co.kirikiri.roadmap.domain.RoadmapContent;
import co.kirikiri.roadmap.domain.RoadmapNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadmapNodeRepository extends JpaRepository<RoadmapNode, Long> {

    List<RoadmapNode> findAllByRoadmapContent(final RoadmapContent roadmapContent);
}
