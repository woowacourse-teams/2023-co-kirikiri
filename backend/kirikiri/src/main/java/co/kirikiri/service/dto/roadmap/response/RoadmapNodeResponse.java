package co.kirikiri.service.dto.roadmap.response;

import java.util.List;

public record RoadmapNodeResponse(
        Long id,
        String title,
        String description,
        List<String> imageUrls
) {

}
