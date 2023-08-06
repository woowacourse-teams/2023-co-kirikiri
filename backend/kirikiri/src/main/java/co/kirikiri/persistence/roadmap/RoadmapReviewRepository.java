package co.kirikiri.persistence.roadmap;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapReview;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapReviewRepository extends JpaRepository<RoadmapReview, Long> {

    Optional<RoadmapReview> findByRoadmapAndMember(final Roadmap roadmap, final Member member);

    List<RoadmapReview> findByRoadmapOrderByUpdatedAtDesc(final Roadmap roadmap);
}
