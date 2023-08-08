package co.kirikiri.service.dto.goalroom.response;

import co.kirikiri.service.dto.member.response.MemberNameAndImageResponse;

public record GoalRoomCheckFeedResponse(
        MemberNameAndImageResponse member,
        CheckFeedResponse checkFeed
) {

}
