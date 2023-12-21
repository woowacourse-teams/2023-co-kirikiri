package co.kirikiri.goalroom.service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MemberGoalRoomForListDto(
        Long goalRoomId,
        String name,
        String goalRoomStatus,
        Integer currentMemberCount,
        Integer limitedMemberCount,
        LocalDateTime createdAt,
        LocalDate startDate,
        LocalDate endDate,
        MemberDto goalRoomLeader
) {

}
