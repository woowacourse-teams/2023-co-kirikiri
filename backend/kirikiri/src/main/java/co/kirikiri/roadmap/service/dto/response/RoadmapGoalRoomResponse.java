package co.kirikiri.roadmap.service.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RoadmapGoalRoomResponse(
        Long goalRoomId,
        String name,
        String goalRoomStatus,
        Integer currentMemberCount,
        Integer limitedMemberCount,
        LocalDateTime createdAt,
        LocalDate startDate,
        LocalDate endDate,
        MemberResponse goalRoomLeader
) {

}
