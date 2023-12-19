package co.kirikiri.roadmap.service.dto.response;

import java.util.List;

public record MemberRoadmapResponses(
        List<MemberRoadmapResponse> responses,
        boolean hasNext
) {

}
