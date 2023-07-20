package co.kirikiri.service.dto.goalroom.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record GoalRoomRoadmapNodeRequest(

        Long roadmapNodeId,

        Integer checkCount,

        @JsonFormat(pattern = "yyMMdd")
        LocalDate startDate,

        @JsonFormat(pattern = "yyMMdd")
        LocalDate endDate
) {
}
