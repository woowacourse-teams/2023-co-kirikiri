package co.kirikiri.service.dto.goalroom;

public enum GoalRoomMemberSortTypeDto {

    JOINED_ASC("골룸 입장 순 (오래된 순)"),
    JOINED_DESC("골룸 입장 순 (최신순)"),
    ACCOMPLISHMENT_RATE("달성률 순");

    private final String description;

    GoalRoomMemberSortTypeDto(final String description) {
        this.description = description;
    }
}
