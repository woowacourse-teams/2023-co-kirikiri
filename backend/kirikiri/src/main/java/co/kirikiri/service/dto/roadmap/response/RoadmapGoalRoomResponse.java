package co.kirikiri.service.dto.roadmap.response;

import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.service.dto.member.response.MemberResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RoadmapGoalRoomResponse(
        Long goalRoomId,
        String name,
        GoalRoomStatus status,
        Integer currentMemberCount,
        Integer limitedMemberCount,
        LocalDateTime createdAt,
        LocalDate startDate,
        LocalDate endDate,
        MemberResponse goalRoomLeader
) {

}
