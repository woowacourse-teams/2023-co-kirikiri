package co.kirikiri.persistence.roadmap;

import co.kirikiri.domain.roadmap.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long>, RoadmapQueryRepository {

}
