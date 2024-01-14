package co.kirikiri.roadmap.persistence;

import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapCategory;
import co.kirikiri.roadmap.persistence.dto.RoadmapOrderType;
import co.kirikiri.roadmap.persistence.dto.RoadmapSearchDto;

import java.util.List;
import java.util.Optional;

public interface RoadmapQueryRepository {

    Optional<Roadmap> findRoadmapById(final Long roadmapId);

    List<Roadmap> findRoadmapsByCategory(final RoadmapCategory category,
                                         final RoadmapOrderType orderType,
                                         final Long lastId,
                                         final int pageSize);

    List<Roadmap> findRoadmapsByCond(final RoadmapSearchDto searchRequest,
                                     final RoadmapOrderType orderType,
                                     final Long lastId,
                                     final int pageSize);

    List<Roadmap> findRoadmapsWithCategoryByMemberIdOrderByLatest(final Long memberId,
                                                                  final Long lastId,
                                                                  final int pageSize);

    Optional<Roadmap> findByIdAndMemberIdentifier(final Long roadmapId, final String identifier);
}
