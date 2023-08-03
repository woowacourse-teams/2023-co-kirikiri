package co.kirikiri.service.dto.goalroom.request;

public enum GoalRoomStatusTypeRequest {
    RECRUITING("모집 중"),
    RUNNING("진행 중"),
    COMPLETED("완료");

    private final String description;

    GoalRoomStatusTypeRequest(final String description) {
        this.description = description;
    }
}
