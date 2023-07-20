package co.kirikiri.service.mapper;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.dto.GoalRoomFilterType;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.goalroom.GoalRoomFilterTypeDto;
import co.kirikiri.service.dto.goalroom.response.GoalRoomForListResponse;
import co.kirikiri.service.dto.member.MemberResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public class GoalRoomMapper {

    public static GoalRoomFilterType convertToGoalRoomFilterType(final GoalRoomFilterTypeDto filterType) {
        if (filterType == null) {
            return GoalRoomFilterType.LATEST;
        }
        try {
            return GoalRoomFilterType.valueOf(filterType.name());
        } catch (final IllegalArgumentException e) {
            throw new NotFoundException("존재하지 않는 정렬 조건입니다. filterType = " + filterType);
        }
    }

    public static PageResponse<GoalRoomForListResponse> convertToGoalRoomsPageResponse(
            final Page<GoalRoom> goalRoomsPage,
            final CustomPageRequest pageRequest) {
        final int currentPage = pageRequest.getOriginPage();
        final int totalPages = goalRoomsPage.getTotalPages();
        final List<GoalRoomForListResponse> goalRoomForListResponses = goalRoomsPage.getContent().stream()
                .map(GoalRoomMapper::convertToGoalRoomForListResponse)
                .toList();
        return new PageResponse<>(currentPage, totalPages, goalRoomForListResponses);
    }

    private static GoalRoomForListResponse convertToGoalRoomForListResponse(final GoalRoom goalRoom) {
        return new GoalRoomForListResponse(goalRoom.getId(), goalRoom.getName(),
                goalRoom.getCurrentPendingMemberCount(),
                goalRoom.getLimitedMemberCount(), goalRoom.getCreatedAt(), goalRoom.getGoalRoomStartDate(),
                goalRoom.getGoalRoomEndDate(), convertToMemberResponse(goalRoom));
    }

    private static MemberResponse convertToMemberResponse(final GoalRoom goalRoom) {
        final Member goalRoomLeader = goalRoom.getGoalRoomPendingMembers().findGoalRoomLeader();
        return new MemberResponse(goalRoomLeader.getId(), goalRoomLeader.getNickname().getValue());
    }
}
