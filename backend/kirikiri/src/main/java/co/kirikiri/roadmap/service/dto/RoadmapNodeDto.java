package co.kirikiri.roadmap.service.dto;

import java.util.List;

public record RoadmapNodeDto(
        Long id,
        String title,
        String description,
        List<String> imageUrls
) {

}
