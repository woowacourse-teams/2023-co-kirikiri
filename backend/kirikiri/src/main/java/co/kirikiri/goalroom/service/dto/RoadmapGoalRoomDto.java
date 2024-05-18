package co.kirikiri.goalroom.service.dto;

import co.kirikiri.goalroom.domain.GoalRoomStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RoadmapGoalRoomDto(
        Long goalRoomId,
        String name,
        GoalRoomStatus status,
        Integer currentMemberCount,
        Integer limitedMemberCount,
        LocalDateTime createdAt,
        LocalDate startDate,
        LocalDate endDate,
        MemberDto goalRoomLeader
) {

}
