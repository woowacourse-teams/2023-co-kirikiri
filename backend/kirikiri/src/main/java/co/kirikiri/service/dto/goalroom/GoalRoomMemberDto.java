package co.kirikiri.service.dto.goalroom;

public record GoalRoomMemberDto(
        Long memberId,
        String nickname,
        String imagePath,
        Double accomplishmentRate
) {

}
