package co.kirikiri.persistence;

import co.kirikiri.domain.roadmap.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {

}
