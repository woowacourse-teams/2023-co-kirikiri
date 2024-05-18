package co.kirikiri.goalroom.service.dto.response;

import java.time.LocalDate;
import java.util.List;

public record MemberGoalRoomResponse(
        String name,
        String status,
        Long leaderId,
        Integer currentMemberCount,
        Integer limitedMemberCount,
        LocalDate startDate,
        LocalDate endDate,
        Long roadmapContentId,
        GoalRoomRoadmapNodesResponse goalRoomRoadmapNodes,
        List<DashBoardToDoResponse> goalRoomTodos,
        List<DashBoardCheckFeedResponse> checkFeeds
) {

}
