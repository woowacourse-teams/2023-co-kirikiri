package co.kirikiri.roadmap.persistence;

import co.kirikiri.roadmap.domain.RoadmapCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoadmapCategoryRepository extends JpaRepository<RoadmapCategory, Long> {

    Optional<RoadmapCategory> findByName(final String name);
}
