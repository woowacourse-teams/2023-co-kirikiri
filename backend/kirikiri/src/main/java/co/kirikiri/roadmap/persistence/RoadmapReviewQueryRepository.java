package co.kirikiri.roadmap.persistence;

import co.kirikiri.roadmap.domain.RoadmapReview;
import java.util.List;

public interface RoadmapReviewQueryRepository {

    List<RoadmapReview> findRoadmapReviewByRoadmapIdOrderByLatest(final Long roadmapId,
                                                                  final Long lastId,
                                                                  final int pageSize);
}
