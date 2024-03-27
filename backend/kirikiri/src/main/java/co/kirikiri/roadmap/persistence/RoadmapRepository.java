package co.kirikiri.roadmap.persistence;

import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long>, RoadmapQueryRepository {

    List<Roadmap> findByStatus(final RoadmapStatus status);
}
