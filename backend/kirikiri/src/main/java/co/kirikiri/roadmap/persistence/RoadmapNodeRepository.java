package co.kirikiri.roadmap.persistence;

import co.kirikiri.roadmap.domain.RoadmapNode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapNodeRepository extends JpaRepository<RoadmapNode, Long>, RoadmapNodeQueryRepository {

}
