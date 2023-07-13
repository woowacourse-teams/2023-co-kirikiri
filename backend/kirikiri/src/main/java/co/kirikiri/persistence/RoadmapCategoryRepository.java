package co.kirikiri.persistence;

import co.kirikiri.domain.roadmap.RoadmapCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapCategoryRepository extends JpaRepository<RoadmapCategory, Long> {

}
