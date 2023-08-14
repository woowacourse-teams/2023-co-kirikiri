package co.kirikiri.service.dto.member.response;

import co.kirikiri.service.dto.goalroom.response.CheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodesResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomTodoResponse;
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
        List<GoalRoomTodoResponse> goalRoomTodos,
        List<CheckFeedResponse> checkFeeds
) {

}
