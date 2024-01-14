package co.kirikiri.goalroom.service.dto;

public record GoalRoomMemberDto(
        Long memberId,
        String nickname,
        String imagePath,
        Double accomplishmentRate
) {

}
