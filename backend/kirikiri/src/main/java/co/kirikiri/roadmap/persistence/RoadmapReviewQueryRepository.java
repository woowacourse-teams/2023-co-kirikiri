package co.kirikiri.roadmap.persistence;

import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapReview;
import java.util.List;

public interface RoadmapReviewQueryRepository {

    List<RoadmapReview> findRoadmapReviewWithMemberByRoadmapOrderByLatest(final Roadmap roadmap,
                                                                          final Long lastId,
                                                                          final int pageSize);
}
