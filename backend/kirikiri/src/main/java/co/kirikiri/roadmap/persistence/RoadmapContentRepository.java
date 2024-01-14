package co.kirikiri.roadmap.persistence;

import co.kirikiri.roadmap.domain.RoadmapContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoadmapContentRepository extends JpaRepository<RoadmapContent, Long> {

    Optional<RoadmapContent> findFirstByRoadmapIdOrderByCreatedAtDesc(final Long roadmapId);

    List<RoadmapContent> findAllByRoadmapId(final Long roadmapId);

    void deleteAllByRoadmapId(final Long roadmapId);
}
