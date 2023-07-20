package co.kirikiri.service.dto.goalroom.response;

import co.kirikiri.service.dto.member.MemberResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record GoalRoomForListResponse(
        Long goalRoomId,
        String name,
        Integer currentMemberCount,
        Integer limitedMemberCount,
        LocalDateTime createdAt,
        LocalDate startDate,
        LocalDate endDate,
        MemberResponse goalRoomLeader
) {

}
