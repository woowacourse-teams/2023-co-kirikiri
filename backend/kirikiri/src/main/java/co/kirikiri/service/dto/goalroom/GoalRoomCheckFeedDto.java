package co.kirikiri.service.dto.goalroom;

import co.kirikiri.service.dto.member.MemberDto;

public record GoalRoomCheckFeedDto(
        MemberDto memberDto,
        CheckFeedDto checkFeedDto
) {

}
