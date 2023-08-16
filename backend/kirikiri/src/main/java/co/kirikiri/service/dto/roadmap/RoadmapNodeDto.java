package co.kirikiri.service.dto.roadmap;

import java.util.List;

public record RoadmapNodeDto(
        Long id,
        String title,
        String description,
        List<String> imageUrls
) {

}
