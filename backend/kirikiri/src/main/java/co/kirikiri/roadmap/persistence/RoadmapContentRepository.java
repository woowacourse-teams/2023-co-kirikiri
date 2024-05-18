package co.kirikiri.roadmap.persistence;

import co.kirikiri.roadmap.domain.RoadmapContent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapContentRepository extends JpaRepository<RoadmapContent, Long>, RoadmapContentQueryRepository {

    Optional<RoadmapContent> findFirstByRoadmapIdOrderByCreatedAtDesc(final Long roadmapId);

    List<RoadmapContent> findAllByRoadmapId(final Long roadmapId);

    void deleteAllByRoadmapId(final Long roadmapId);
}
