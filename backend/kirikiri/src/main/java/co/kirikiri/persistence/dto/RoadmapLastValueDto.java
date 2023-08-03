package co.kirikiri.persistence.dto;

import co.kirikiri.service.dto.CustomScrollRequest;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RoadmapLastValueDto {

    private final LocalDateTime LastCreatedAt;
    private final Long LastGoalRoomCount;
    private final Long LastParticipatedCount;
    private final Double LastReviewRate;

    public static RoadmapLastValueDto create(final CustomScrollRequest request) {
        if (request.lastCreatedAt() == null && request.lastGoalRoomCount() == null
                && request.lastParticipatedCount() == null && request.lastReviewRate() == null) {
            return null;
        }
        if (request.lastGoalRoomCount() != null) {
            return new RoadmapLastValueDto(null, request.lastGoalRoomCount(), null, null);
        }
        if (request.lastParticipatedCount() != null) {
            return new RoadmapLastValueDto(null, null, request.lastParticipatedCount(), null);
        }
        if (request.lastReviewRate() != null) {
            return new RoadmapLastValueDto(null, null, null, request.lastReviewRate());
        }
        return new RoadmapLastValueDto(request.lastCreatedAt(), null, null, null);
    }
}
