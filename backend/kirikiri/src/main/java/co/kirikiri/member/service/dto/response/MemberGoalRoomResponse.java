package co.kirikiri.member.service.dto.response;

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
        MemberGoalRoomRoadmapNodesResponse goalRoomRoadmapNodes,
        List<MemberGoalRoomTodoResponse> goalRoomTodos,
        List<MemberCheckFeedResponse> checkFeeds
) {

}
