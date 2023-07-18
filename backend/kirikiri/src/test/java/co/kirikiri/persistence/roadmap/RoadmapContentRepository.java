package co.kirikiri.persistence.roadmap;

import co.kirikiri.domain.roadmap.RoadmapContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapContentRepository extends JpaRepository<RoadmapContent, Long> {

}
