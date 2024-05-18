package co.kirikiri.roadmap.persistence;

import co.kirikiri.roadmap.domain.RoadmapNode;
import java.util.Optional;

public interface RoadmapNodeQueryRepository {

    Optional<RoadmapNode> findByIdWithRoadmapNodeImages(final Long id);
}
