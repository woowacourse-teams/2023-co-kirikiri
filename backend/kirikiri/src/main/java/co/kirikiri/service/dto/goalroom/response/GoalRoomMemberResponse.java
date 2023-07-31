package co.kirikiri.service.dto.goalroom.response;

public record GoalRoomMemberResponse(
        Long memberId,
        String nickname,
        String imagePath,
        Double accomplishmentRate
) {
}
