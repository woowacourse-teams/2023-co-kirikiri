package co.kirikiri.roadmap.persistence;

import co.kirikiri.roadmap.domain.RoadmapContent;
import java.util.Optional;

public interface RoadmapContentQueryRepository {

    Optional<RoadmapContent> findByIdWithRoadmapNodes(final Long id);
}
