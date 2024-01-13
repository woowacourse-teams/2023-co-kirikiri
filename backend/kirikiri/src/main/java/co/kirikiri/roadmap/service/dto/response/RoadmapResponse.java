package co.kirikiri.roadmap.service.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record RoadmapResponse(
        Long roadmapId,
        RoadmapCategoryResponse category,
        String roadmapTitle,
        String introduction,
        MemberResponse creator,
        RoadmapContentResponse content,
        String difficulty,
        int recommendedRoadmapPeriod,
        LocalDateTime createdAt,
        List<RoadmapTagResponse> tags,
        Long recruitedGoalRoomNumber,
        Long runningGoalRoomNumber,
        Long completedGoalRoomNumber
) {

}
