package co.kirikiri.member.service.dto.response;

import java.time.LocalDate;

public record MemberGoalRoomRoadmapNodeResponse(
        Long id,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        Integer checkCount
) {
}
