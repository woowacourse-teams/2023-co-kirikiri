package co.kirikiri.persistence.roadmap;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.persistence.roadmap.dto.RoadmapFilterType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoadmapQueryRepository {

    Page<Roadmap> findRoadmapPagesByCond(final RoadmapCategory category, final RoadmapFilterType orderType,
                                         final Pageable pageable);

    List<Roadmap> findRoadmapsWithCategoryByMemberOrderByLatest(final Member member, final Long lastValue,
                                                                final int pageSize);
}
