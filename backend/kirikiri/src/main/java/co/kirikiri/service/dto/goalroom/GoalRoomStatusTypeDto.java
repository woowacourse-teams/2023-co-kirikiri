package co.kirikiri.service.dto.goalroom;

public enum GoalRoomStatusTypeDto {
    RECRUITING("모집 중"),
    RUNNING("진행 중"),
    COMPLETED("완료");

    private final String description;

    GoalRoomStatusTypeDto(final String description) {
        this.description = description;
    }
}
