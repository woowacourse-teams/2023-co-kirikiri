package co.kirikiri.persistence.roadmap;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.persistence.dto.RoadmapFilterType;
import co.kirikiri.persistence.dto.RoadmapLastValueDto;
import co.kirikiri.persistence.dto.RoadmapSearchDto;
import co.kirikiri.persistence.dto.RoadmapStatusType;
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

    List<Roadmap> findRoadmapsWithCategoryByMemberOrderByLatest(final Member member,
                                                                final RoadmapLastValueDto lastValue,
                                                                final int pageSize);

    Optional<Roadmap> findByIdAndMemberIdentifier(final Long roadmapId, final String identifier);

    List<Roadmap> findWithRoadmapContentByStatus(final RoadmapStatusType status);
}
