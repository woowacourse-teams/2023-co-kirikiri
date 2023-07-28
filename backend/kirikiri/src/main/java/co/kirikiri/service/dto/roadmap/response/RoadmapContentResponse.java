package co.kirikiri.service.dto.roadmap.response;

import java.util.List;

public record RoadmapContentResponse(
        String content,
        List<RoadmapNodeResponse> nodes
) {

}
