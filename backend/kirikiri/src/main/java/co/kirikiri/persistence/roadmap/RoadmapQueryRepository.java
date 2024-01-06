package co.kirikiri.persistence.roadmap;

import co.kirikiri.member.domain.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.persistence.dto.RoadmapOrderType;
import co.kirikiri.persistence.dto.RoadmapSearchDto;
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

    List<Roadmap> findRoadmapsWithCategoryByMemberOrderByLatest(final Member member,
                                                                final Long lastId,
                                                                final int pageSize);

    Optional<Roadmap> findByIdAndMemberIdentifier(final Long roadmapId, final String identifier);

    List<Roadmap> findWithRoadmapContentByStatus(final RoadmapStatus status);
}
