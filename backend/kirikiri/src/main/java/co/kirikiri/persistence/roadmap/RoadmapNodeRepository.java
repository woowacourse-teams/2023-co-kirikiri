package co.kirikiri.persistence.roadmap;

import co.kirikiri.domain.roadmap.RoadmapNode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapNodeRepository extends JpaRepository<RoadmapNode, Long> {

}
