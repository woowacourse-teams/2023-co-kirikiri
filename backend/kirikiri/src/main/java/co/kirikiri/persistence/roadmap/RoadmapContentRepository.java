package co.kirikiri.persistence.roadmap;

import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapContent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoadmapContentRepository extends JpaRepository<RoadmapContent, Long> {

    Optional<RoadmapContent> findFirstByRoadmapOrderByCreatedAtDesc(final Roadmap roadmap);

    @Query("select rc from RoadmapContent rc "
            + "join fetch rc.roadmap r "
            + "where rc.id = :roadmapContentId")
    Optional<RoadmapContent> findByIdWithRoadmap(@Param("roadmapContentId") final Long roadmapContentId);
}
