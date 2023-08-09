package co.kirikiri.service.dto.roadmap.response;

import co.kirikiri.service.dto.member.response.MemberResponse;
import java.time.LocalDateTime;
import java.util.List;

public record RoadmapForListResponses(
       List<RoadmapForListResponse> responses,
       boolean hasNext
) {

}
