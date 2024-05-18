package co.kirikiri.roadmap.service.dto.response;

import java.util.List;

public record RoadmapForListResponses(
        List<RoadmapForListResponse> responses,
        boolean hasNext
) {

}
