package co.kirikiri.persistence.dto;

import co.kirikiri.service.dto.CustomScrollRequest;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class GoalRoomLastValueDto {

    private final LocalDateTime lastCreatedAt;

    public static GoalRoomLastValueDto create(final CustomScrollRequest request) {
        if (request.lastCreatedAt() == null) {
            return null;
        }
        return new GoalRoomLastValueDto(request.lastCreatedAt());
    }
}
