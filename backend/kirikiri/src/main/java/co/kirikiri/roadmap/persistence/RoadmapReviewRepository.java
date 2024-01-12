package co.kirikiri.roadmap.persistence;

import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoadmapReviewRepository extends JpaRepository<RoadmapReview, Long>, RoadmapReviewQueryRepository {

    Optional<RoadmapReview> findByRoadmapAndMemberId(final Roadmap roadmap, final Long memberId);
}
