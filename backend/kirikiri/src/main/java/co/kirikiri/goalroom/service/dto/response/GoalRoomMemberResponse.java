package co.kirikiri.goalroom.service.dto.response;

public record GoalRoomMemberResponse(
        Long memberId,
        String nickname,
        String imagePath,
        Double accomplishmentRate
) {

}
