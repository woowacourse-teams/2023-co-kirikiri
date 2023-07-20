package co.kirikiri.service.dto.roadmap.response;

import java.util.List;

public record RoadmapNodeResponse(
        String title,
        String description,
        List<String> imageUrls
) {

}
