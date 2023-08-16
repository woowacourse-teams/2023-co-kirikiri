package co.kirikiri.service.dto.roadmap;

public enum RoadmapGoalRoomsOrderTypeDto {

    LATEST("최신순"),
    PARTICIPATION_RATE("참가율 순");

    private final String description;

    RoadmapGoalRoomsOrderTypeDto(final String description) {
        this.description = description;
    }
}
