package co.kirikiri.service.dto.goalroom;

public enum GoalRoomFilterTypeDto {

    LATEST("최신순"),
    PARTICIPANT_COUNT("참여 중인 인원순");

    private final String description;

    GoalRoomFilterTypeDto(final String description) {
        this.description = description;
    }
}
