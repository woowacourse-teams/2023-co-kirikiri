package co.kirikiri.service.dto.roadmap;

public enum RoadmapGoalRoomsFilterTypeDto {

    LATEST("최신순"),
    PARTICIPATION_RATE("참가율 순");

    private final String description;

    RoadmapGoalRoomsFilterTypeDto(final String description) {
        this.description = description;
    }
}
