package co.kirikiri.service.dto.goalroom.request;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GoalRoomStatusTypeRequest {
    RECRUITING("모집 중"),
    RUNNING("진행 중"),
    COMPLETED("완료");

    private final String description;

    @JsonCreator
    GoalRoomStatusTypeRequest(final String description) {
        this.description = description;
    }
}
