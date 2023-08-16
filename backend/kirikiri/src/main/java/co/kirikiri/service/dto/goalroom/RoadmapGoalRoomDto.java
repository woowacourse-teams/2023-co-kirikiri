package co.kirikiri.service.dto.goalroom;

import co.kirikiri.service.dto.member.MemberDto;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RoadmapGoalRoomDto(
        Long goalRoomId,
        String name,
        Integer currentMemberCount,
        Integer limitedMemberCount,
        LocalDateTime createdAt,
        LocalDate startDate,
        LocalDate endDate,
        MemberDto goalRoomLeader
) {

}
