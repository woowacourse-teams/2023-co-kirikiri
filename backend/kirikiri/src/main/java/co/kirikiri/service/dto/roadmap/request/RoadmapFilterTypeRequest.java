package co.kirikiri.service.dto.roadmap.request;

public enum RoadmapFilterTypeRequest {
    LATEST("최신순"),
    GOAL_ROOM_COUNT("생성된 골룸이 많은순"),
    PARTICIPANT_COUNT("참여 중인 인원순"),
    REVIEW_RATE("평점순");

    private final String description;

    RoadmapFilterTypeRequest(final String description) {
        this.description = description;
    }
}
