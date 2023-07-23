package co.kirikiri.service.dto.goalroom.response;

import co.kirikiri.service.dto.member.response.MemberResponse;
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
        MemberResponse goalRoomLeader,
        String status
) {

    public GoalRoomForListResponse(
            final Long goalRoomId,
            final String name,
            final Integer currentMemberCount,
            final Integer limitedMemberCount,
            final LocalDateTime createdAt,
            final LocalDate startDate,
            final LocalDate endDate,
            final MemberResponse goalRoomLeader
    ) {
        this(goalRoomId, name, currentMemberCount, limitedMemberCount, createdAt, startDate, endDate, goalRoomLeader,
                null);
    }
}