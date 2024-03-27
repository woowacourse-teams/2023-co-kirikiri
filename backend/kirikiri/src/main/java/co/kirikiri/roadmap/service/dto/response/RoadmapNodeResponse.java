package co.kirikiri.roadmap.service.dto.response;

import java.util.List;

public record RoadmapNodeResponse(
        Long id,
        String title,
        String description,
        List<String> imageUrls
) {

}
