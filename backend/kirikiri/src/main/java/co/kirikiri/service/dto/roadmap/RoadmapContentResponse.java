package co.kirikiri.service.dto.roadmap;

import java.util.List;

public record RoadmapContentResponse(
        String content,
        List<RoadmapNodeResponse> nodes
) {

}
