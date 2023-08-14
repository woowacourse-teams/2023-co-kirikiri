package co.kirikiri.service.dto.roadmap.response;

import java.util.List;

public record MemberRoadmapResponses(
        List<MemberRoadmapResponse> responses,
        boolean hasNext
) {

}
