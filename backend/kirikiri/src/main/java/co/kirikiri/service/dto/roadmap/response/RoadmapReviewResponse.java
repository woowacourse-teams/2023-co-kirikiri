package co.kirikiri.service.dto.roadmap.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record RoadmapReviewResponse(
        Long id,
        String name,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt,
        String content,
        Double rate
) {

}