package co.kirikiri.service.dto.member.response;

import co.kirikiri.service.dto.goalroom.response.CheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodesResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomTodoResponse;
import java.util.List;

public record MemberGoalRoomResponse(
        String name,
        String status,
        Integer currentMemberCount,
        Integer limitedMemberCount,
        Integer period,
        Long roadmapContentId,
        GoalRoomRoadmapNodesResponse goalRoomRoadmapNodes,
        List<GoalRoomTodoResponse> goalRoomTodos,
        List<CheckFeedResponse> checkFeeds
) {

}
