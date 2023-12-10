package co.kirikiri.service.dto.goalroom;

import java.time.LocalDate;
import java.util.List;

public record GoalRoomRoadmapNodeDetailDto(
        Long id,
        String title,
        String description,
        List<String> imageUrls,
        LocalDate startDate,
        LocalDate endDate,
        Integer checkCount
) {

}
