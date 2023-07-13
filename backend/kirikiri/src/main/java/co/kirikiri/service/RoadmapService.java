package co.kirikiri.service;

import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.persistence.RoadmapCategoryRepository;
import co.kirikiri.service.dto.RoadmapCategoryResponse;
import co.kirikiri.service.mapper.RoadmapMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoadmapService {

    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public List<RoadmapCategoryResponse> getAllRoadmapCategories() {
        final List<RoadmapCategory> roadmapCategories = roadmapCategoryRepository.findAll();
        return RoadmapMapper.convertRoadmapCategoryResponses(roadmapCategories);
    }
}
