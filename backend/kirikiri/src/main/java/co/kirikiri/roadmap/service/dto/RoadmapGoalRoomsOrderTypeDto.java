package co.kirikiri.roadmap.service.dto;

public enum RoadmapGoalRoomsOrderTypeDto {

    LATEST("최신순"),
    CLOSE_TO_DEADLINE("마감임박 순");

    private final String description;

    RoadmapGoalRoomsOrderTypeDto(final String description) {
        this.description = description;
    }
}
