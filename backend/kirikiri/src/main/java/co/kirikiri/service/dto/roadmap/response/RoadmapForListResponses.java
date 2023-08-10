package co.kirikiri.service.dto.roadmap.response;

import java.util.List;

public record RoadmapForListResponses(
        List<RoadmapForListResponse> responses,
        boolean hasNext
) {

}
