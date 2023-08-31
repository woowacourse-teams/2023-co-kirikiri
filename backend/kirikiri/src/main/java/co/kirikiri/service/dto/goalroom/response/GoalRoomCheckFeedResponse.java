package co.kirikiri.service.dto.goalroom.response;

import co.kirikiri.service.dto.member.response.MemberResponse;

public record GoalRoomCheckFeedResponse(
        MemberResponse member,
        CheckFeedResponse checkFeed
) {

}
