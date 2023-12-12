package co.kirikiri.service.dto.goalroom.response;

import co.kirikiri.member.service.dto.response.MemberResponse;

public record GoalRoomCheckFeedResponse(
        MemberResponse member,
        CheckFeedResponse checkFeed
) {

}
