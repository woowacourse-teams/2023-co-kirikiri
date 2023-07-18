package co.kirikiri.persistence.roadmap;

import co.kirikiri.domain.roadmap.RoadmapCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapCategoryRepository extends JpaRepository<RoadmapCategory, Long> {

}
