package co.kirikiri.service.dto.roadmap;

public enum RoadmapFilterType {
    LATEST("최신순"),
    GOAL_ROOM_COUNT("생성된 골룸이 많은순"),
    PARTICIPANT_COUNT("참여 중인 인원순");

    private final String description;

    RoadmapFilterType(final String description) {
        this.description = description;
    }
}
