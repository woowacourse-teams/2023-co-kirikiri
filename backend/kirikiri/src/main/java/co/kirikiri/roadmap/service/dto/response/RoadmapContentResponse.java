package co.kirikiri.roadmap.service.dto.response;

import java.util.List;

public record RoadmapContentResponse(
        Long id,
        String content,
        List<RoadmapNodeResponse> nodes
) {

}
