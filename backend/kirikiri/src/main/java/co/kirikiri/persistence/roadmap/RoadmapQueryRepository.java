package co.kirikiri.persistence.roadmap;

import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.dto.RoadmapFilterType;
import java.util.List;
import java.util.Optional;

public interface RoadmapQueryRepository {

    Optional<Roadmap> findRoadmapById(final Long roadmapId);

    List<Roadmap> findRoadmapPagesByCond(final RoadmapCategory category, final RoadmapFilterType orderType,
                                         final Long lastRoadmapId, final int pageSize);
}
