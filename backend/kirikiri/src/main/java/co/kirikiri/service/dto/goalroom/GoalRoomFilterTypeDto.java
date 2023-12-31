package co.kirikiri.service.dto.goalroom;

public enum GoalRoomFilterTypeDto {

    LATEST("최신순"),
    PARTICIPATION_RATE("참가율 순");

    private final String description;

    GoalRoomFilterTypeDto(final String description) {
        this.description = description;
    }
}