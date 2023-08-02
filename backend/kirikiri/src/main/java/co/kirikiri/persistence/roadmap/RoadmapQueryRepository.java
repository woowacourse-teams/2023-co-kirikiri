package co.kirikiri.persistence.roadmap;

import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.persistence.dto.RoadmapFilterType;
import co.kirikiri.persistence.dto.RoadmapLastValueDto;
import co.kirikiri.persistence.dto.RoadmapSearchDto;
import java.util.List;
import java.util.Optional;

public interface RoadmapQueryRepository {

    Optional<Roadmap> findRoadmapById(final Long roadmapId);

    List<Roadmap> findRoadmapsByCategory(final RoadmapCategory category,
                                         final RoadmapFilterType orderType,
                                         final RoadmapLastValueDto lastValue,
                                         final int pageSize);

    List<Roadmap> findRoadmapsByCond(final RoadmapSearchDto searchRequest,
                                     final RoadmapFilterType orderType,
                                     final RoadmapLastValueDto lastValue,
                                     final int pageSize);
}
