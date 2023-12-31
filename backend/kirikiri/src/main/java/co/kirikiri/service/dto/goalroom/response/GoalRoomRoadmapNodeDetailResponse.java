package co.kirikiri.service.dto.goalroom.response;

import java.time.LocalDate;
import java.util.List;

public record GoalRoomRoadmapNodeDetailResponse(
        Long id,
        String title,
        String description,
        List<String> imageUrls,
        LocalDate startDate,
        LocalDate endDate,
        Integer checkCount
) {

}
