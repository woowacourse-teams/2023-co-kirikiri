package co.kirikiri.checkfeed.service.dto.response;

public record GoalRoomCheckFeedResponse(
        CheckFeedMemberResponse member,
        CheckFeedResponse checkFeed
) {

}
