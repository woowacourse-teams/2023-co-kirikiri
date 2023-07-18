package co.kirikiri.persistence.roadmap;

import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapContent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapContentRepository extends JpaRepository<RoadmapContent, Long> {

    Optional<RoadmapContent> findFirstByRoadmapOrderByCreatedAtDesc(final Roadmap roadmap);
}
