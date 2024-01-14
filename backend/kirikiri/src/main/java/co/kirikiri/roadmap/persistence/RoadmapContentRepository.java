package co.kirikiri.roadmap.persistence;

import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoadmapContentRepository extends JpaRepository<RoadmapContent, Long> {

    Optional<RoadmapContent> findFirstByRoadmapIdOrderByCreatedAtDesc(final Long roadmapId);

    Optional<RoadmapContent> findFirstByRoadmapOrderByCreatedAtDesc(final Roadmap roadmap);

    @Query("select rc from RoadmapContent rc "
            + "join fetch rc.roadmap r "
            + "where rc.id = :roadmapContentId")
    Optional<RoadmapContent> findByIdWithRoadmap(@Param("roadmapContentId") final Long roadmapContentId);
}
