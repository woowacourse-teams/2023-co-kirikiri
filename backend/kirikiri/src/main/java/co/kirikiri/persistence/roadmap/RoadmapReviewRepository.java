package co.kirikiri.persistence.roadmap;

import co.kirikiri.member.domain.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoadmapReviewRepository extends JpaRepository<RoadmapReview, Long>, RoadmapReviewQueryRepository {

    Optional<RoadmapReview> findByRoadmapAndMember(final Roadmap roadmap, final Member member);
}
