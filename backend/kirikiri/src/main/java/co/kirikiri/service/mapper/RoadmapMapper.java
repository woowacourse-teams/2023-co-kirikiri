package co.kirikiri.service.mapper;

import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.service.dto.RoadmapCategoryResponse;
import java.util.List;

public class RoadmapMapper {

    public static List<RoadmapCategoryResponse> convertRoadmapCategoryResponses(
        final List<RoadmapCategory> roadmapCategories) {
        return roadmapCategories.stream()
            .map(category -> new RoadmapCategoryResponse(category.getId(), category.getName()))
            .toList();
    }
}
