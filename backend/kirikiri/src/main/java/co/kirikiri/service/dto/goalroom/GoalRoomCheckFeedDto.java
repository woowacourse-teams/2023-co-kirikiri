package co.kirikiri.service.dto.goalroom;

import co.kirikiri.member.service.dto.MemberDto;

public record GoalRoomCheckFeedDto(
        MemberDto memberDto,
        CheckFeedDto checkFeedDto
) {

}
