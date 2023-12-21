package co.kirikiri.checkfeed.service.mapper;

import co.kirikiri.checkfeed.service.dto.CheckFeedDto;
import co.kirikiri.checkfeed.service.dto.CheckFeedMemberDto;
import co.kirikiri.checkfeed.service.dto.GoalRoomCheckFeedDto;
import co.kirikiri.checkfeed.service.dto.response.CheckFeedMemberResponse;
import co.kirikiri.checkfeed.service.dto.response.CheckFeedResponse;
import co.kirikiri.checkfeed.service.dto.response.GoalRoomCheckFeedResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckFeedMapper {

    public static List<GoalRoomCheckFeedResponse> convertToGoalRoomCheckFeedResponses(
            final List<GoalRoomCheckFeedDto> checkFeeds) {
        return checkFeeds.stream()
                .map(CheckFeedMapper::convertToGoalRoomCheckFeedResponse)
                .toList();
    }

    private static GoalRoomCheckFeedResponse convertToGoalRoomCheckFeedResponse(
            final GoalRoomCheckFeedDto goalRoomCheckFeedDto) {
        final CheckFeedMemberDto memberDto = goalRoomCheckFeedDto.memberDto();
        final CheckFeedMemberResponse memberResponse = new CheckFeedMemberResponse(memberDto.id(), memberDto.name(),
                memberDto.imageUrl());

        final CheckFeedDto checkFeedDto = goalRoomCheckFeedDto.checkFeedDto();
        final CheckFeedResponse checkFeedResponse = new CheckFeedResponse(checkFeedDto.id(), checkFeedDto.imageUrl(),
                checkFeedDto.description(), checkFeedDto.createdAt().toLocalDate());

        return new GoalRoomCheckFeedResponse(memberResponse, checkFeedResponse);
    }
}
