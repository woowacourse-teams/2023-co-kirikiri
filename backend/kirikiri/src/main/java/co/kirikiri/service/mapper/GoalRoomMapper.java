package co.kirikiri.service.mapper;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomToDo;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.GoalRoomTodoContent;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.domain.member.Member;
import co.kirikiri.persistence.goalroom.dto.GoalRoomFilterType;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.goalroom.GoalRoomCreateDto;
import co.kirikiri.service.dto.goalroom.GoalRoomFilterTypeDto;
import co.kirikiri.service.dto.goalroom.GoalRoomRoadmapNodeDto;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomForListResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.member.response.MemberResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GoalRoomMapper {

    public static GoalRoomCreateDto convertToGoalRoomCreateDto(final GoalRoomCreateRequest goalRoomCreateRequest) {
        final GoalRoomTodoRequest goalRoomTodoRequest = goalRoomCreateRequest.goalRoomTodo();
        final GoalRoomToDo goalRoomToDo = makeGoalRoomToDo(goalRoomTodoRequest);
        final List<GoalRoomRoadmapNodeRequest> goalRoomRoadmapNodeRequests = goalRoomCreateRequest.goalRoomRoadmapNodeRequests();
        final List<GoalRoomRoadmapNodeDto> goalRoomRoadmapNodeDtos = makeGoalRoomRoadmapNodeDtos(
                goalRoomRoadmapNodeRequests);
        return new GoalRoomCreateDto(goalRoomCreateRequest.roadmapContentId(),
                new GoalRoomName(goalRoomCreateRequest.name()),
                new LimitedMemberCount(goalRoomCreateRequest.limitedMemberCount()), goalRoomToDo,
                goalRoomRoadmapNodeDtos);
    }

    private static GoalRoomToDo makeGoalRoomToDo(final GoalRoomTodoRequest goalRoomTodoRequest) {
        return new GoalRoomToDo(new GoalRoomTodoContent(goalRoomTodoRequest.content()),
                new Period(goalRoomTodoRequest.startDate(), goalRoomTodoRequest.endDate()));
    }

    private static List<GoalRoomRoadmapNodeDto> makeGoalRoomRoadmapNodeDtos(
            final List<GoalRoomRoadmapNodeRequest> goalRoomRoadmapNodeRequests) {
        return goalRoomRoadmapNodeRequests
                .stream()
                .map(it -> new GoalRoomRoadmapNodeDto(it.roadmapNodeId(), it.checkCount(), it.startDate(),
                        it.endDate()))
                .toList();
    }

    public static GoalRoomResponse convertGoalRoomResponse(final GoalRoom goalRoom) {
        final GoalRoomRoadmapNodes nodes = goalRoom.getGoalRoomRoadmapNodes();
        final List<GoalRoomNodeResponse> roadmapNodeResponses = convertGoalRoomNodeResponses(nodes.getValues());
        final int period = goalRoom.calculateTotalPeriod();
        return new GoalRoomResponse(goalRoom.getName().getValue(), goalRoom.getCurrentPendingMemberCount(),
                goalRoom.getLimitedMemberCount().getValue(), roadmapNodeResponses, period);
    }

    private static List<GoalRoomNodeResponse> convertGoalRoomNodeResponses(
            final List<GoalRoomRoadmapNode> roadmapNodes) {
        return roadmapNodes.stream()
                .map(GoalRoomMapper::convertGoalRoomNodeResponse)
                .toList();
    }

    private static GoalRoomNodeResponse convertGoalRoomNodeResponse(final GoalRoomRoadmapNode node) {
        return new GoalRoomNodeResponse(node.getRoadmapNode().getTitle(), node.getStartDate(), node.getEndDate(),
                node.getCheckCount());
    }

    public static GoalRoomCertifiedResponse convertGoalRoomCertifiedResponse(final GoalRoom goalRoom,
                                                                             final boolean isJoined) {
        final GoalRoomRoadmapNodes nodes = goalRoom.getGoalRoomRoadmapNodes();
        final List<GoalRoomNodeResponse> roadmapNodeResponses = convertGoalRoomNodeResponses(nodes.getValues());
        final int period = goalRoom.calculateTotalPeriod();
        return new GoalRoomCertifiedResponse(goalRoom.getName().getValue(), goalRoom.getCurrentPendingMemberCount(),
                goalRoom.getLimitedMemberCount().getValue(), roadmapNodeResponses, period, isJoined);
    }

    public static GoalRoomFilterType convertToGoalRoomFilterType(final GoalRoomFilterTypeDto filterType) {
        if (filterType == null) {
            return GoalRoomFilterType.LATEST;
        }
        return GoalRoomFilterType.valueOf(filterType.name());
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
        return new GoalRoomForListResponse(goalRoom.getId(), goalRoom.getName().getValue(),
                goalRoom.getCurrentPendingMemberCount(),
                goalRoom.getLimitedMemberCount().getValue(), goalRoom.getCreatedAt(), goalRoom.getStartDate(),
                goalRoom.getEndDate(), convertToMemberResponse(goalRoom));
    }

    private static MemberResponse convertToMemberResponse(final GoalRoom goalRoom) {
        final Member goalRoomLeader = goalRoom.findGoalRoomLeaderInPendingMember();
        return new MemberResponse(goalRoomLeader.getId(), goalRoomLeader.getNickname().getValue());
    }
}