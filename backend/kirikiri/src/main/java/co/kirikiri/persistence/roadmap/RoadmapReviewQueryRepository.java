package co.kirikiri.persistence.roadmap;

import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapReview;
import co.kirikiri.persistence.dto.RoadmapReviewLastValueDto;
import java.util.List;

public interface RoadmapReviewQueryRepository {

    List<RoadmapReview> findRoadmapReviewWithMemberByRoadmapOrderByLatest(final Roadmap roadmap,
                                                                          final RoadmapReviewLastValueDto lastValue,
                                                                          final int pageSize);
}
