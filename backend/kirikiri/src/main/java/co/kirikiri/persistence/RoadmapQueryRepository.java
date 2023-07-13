package co.kirikiri.persistence;

import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.dto.RoadmapOrderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoadmapQueryRepository {

    Page<Roadmap> getRoadmapPagesByCond(final RoadmapCategory category, final RoadmapOrderType orderType,
                                        final Pageable pageable);
}
