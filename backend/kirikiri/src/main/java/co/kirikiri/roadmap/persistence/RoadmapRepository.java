package co.kirikiri.roadmap.persistence;

import co.kirikiri.roadmap.domain.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long>, RoadmapQueryRepository {

}
