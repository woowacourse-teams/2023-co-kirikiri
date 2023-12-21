package co.kirikiri.goalroom.service.dto.response;

import co.kirikiri.goalroom.domain.GoalRoomStatus;
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
