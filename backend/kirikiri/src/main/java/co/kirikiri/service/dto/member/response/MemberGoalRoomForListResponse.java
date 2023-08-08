package co.kirikiri.service.dto.member.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MemberGoalRoomForListResponse(
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
