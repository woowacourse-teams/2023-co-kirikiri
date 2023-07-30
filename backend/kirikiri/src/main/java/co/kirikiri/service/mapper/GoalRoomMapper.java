package co.kirikiri.service.mapper;

import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.goalroom.GoalRoomToDo;
import co.kirikiri.domain.goalroom.GoalRoomToDos;
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
import co.kirikiri.service.dto.goalroom.request.GoalRoomStatusTypeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.CheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomForListResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodesResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomTodoResponse;
import co.kirikiri.service.dto.member.response.MemberGoalRoomForListResponse;
import co.kirikiri.service.dto.member.response.MemberGoalRoomResponse;
import co.kirikiri.service.dto.member.response.MemberResponse;
import java.time.LocalDate;
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
        return new GoalRoomResponse(goalRoom.getName().getValue(), goalRoom.getCurrentMemberCount(),
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
        return new GoalRoomCertifiedResponse(goalRoom.getName().getValue(), goalRoom.getCurrentMemberCount(),
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
                goalRoom.getCurrentMemberCount(),
                goalRoom.getLimitedMemberCount().getValue(), goalRoom.getCreatedAt(), goalRoom.getStartDate(),
                goalRoom.getEndDate(), convertToMemberResponse(goalRoom));
    }

    private static MemberResponse convertToMemberResponse(final GoalRoom goalRoom) {
        final Member goalRoomLeader = goalRoom.findGoalRoomLeader();
        return new MemberResponse(goalRoomLeader.getId(), goalRoomLeader.getNickname().getValue());
    }

    public static MemberGoalRoomResponse convertToMemberGoalRoomResponse(final GoalRoom goalRoom,
                                                                         final List<CheckFeed> checkFeeds) {
        final GoalRoomRoadmapNodesResponse nodeResponses = convertToGoalRoomRoadmapNodesResponse(
                goalRoom.getGoalRoomRoadmapNodes());
        final List<GoalRoomTodoResponse> todoResponses = convertGoalRoomTodoResponses(goalRoom.getGoalRoomToDos());
        final List<CheckFeedResponse> checkFeedResponses = convertToCheckFeedResponses(checkFeeds);

        return new MemberGoalRoomResponse(goalRoom.getName().getValue(), goalRoom.getStatus().name(),
                goalRoom.getCurrentMemberCount(), goalRoom.getLimitedMemberCount().getValue(),
                goalRoom.getStartDate(), goalRoom.getEndDate(), goalRoom.getRoadmapContent().getId(),
                nodeResponses, todoResponses, checkFeedResponses);
    }

    private static GoalRoomRoadmapNodesResponse convertToGoalRoomRoadmapNodesResponse(
            final GoalRoomRoadmapNodes nodes) {
        final GoalRoomRoadmapNode firstNode = nodes.getNodeByDate(LocalDate.now());
        if (!nodes.hasBackNode(firstNode)) {
            return new GoalRoomRoadmapNodesResponse(nodes.hasFrontNode(firstNode), nodes.hasBackNode(firstNode),
                    List.of(new GoalRoomRoadmapNodeResponse(firstNode.getRoadmapNode().getTitle(),
                            firstNode.getStartDate(), firstNode.getEndDate(), firstNode.getCheckCount())));
        }

        final GoalRoomRoadmapNode secondNode = nodes.getNodeByDate(firstNode.getEndDate().plusDays(1));
        return new GoalRoomRoadmapNodesResponse(nodes.hasFrontNode(firstNode), nodes.hasBackNode(secondNode),
                List.of(new GoalRoomRoadmapNodeResponse(firstNode.getRoadmapNode().getTitle(),
                                firstNode.getStartDate(), firstNode.getEndDate(), firstNode.getCheckCount()),
                        new GoalRoomRoadmapNodeResponse(secondNode.getRoadmapNode().getTitle(),
                                secondNode.getStartDate(), secondNode.getEndDate(), secondNode.getCheckCount())));
    }

    private static List<GoalRoomTodoResponse> convertGoalRoomTodoResponses(final GoalRoomToDos goalRoomToDos) {
        return goalRoomToDos.getValues()
                .stream()
                .map(todo -> new GoalRoomTodoResponse(todo.getId(), todo.getContent(), todo.getStartDate(),
                        todo.getEndDate()))
                .limit(3)
                .toList();
    }

    private static List<CheckFeedResponse> convertToCheckFeedResponses(final List<CheckFeed> checkFeeds) {
        return checkFeeds.stream()
                .map(checkFeed -> new CheckFeedResponse(checkFeed.getId(), checkFeed.getServerFilePath(),
                        checkFeed.getDescription()))
                .limit(4)
                .toList();
    }

    public static GoalRoomStatus convertToGoalRoomStatus(final GoalRoomStatusTypeRequest statusType) {
        return GoalRoomStatus.valueOf(statusType.name());
    }

    public static List<MemberGoalRoomForListResponse> convertToMemberGoalRoomForListResponses(
            final List<GoalRoom> memberGoalRooms) {
        return memberGoalRooms.stream()
                .map(GoalRoomMapper::convertToMemberGoalRoomForListResponse)
                .toList();
    }

    private static MemberGoalRoomForListResponse convertToMemberGoalRoomForListResponse(final GoalRoom goalRoom) {
        final Member leader = goalRoom.findGoalRoomLeader();
        return new MemberGoalRoomForListResponse(goalRoom.getId(), goalRoom.getName().getValue(),
                goalRoom.getStatus().name(),
                goalRoom.getCurrentMemberCount(), goalRoom.getLimitedMemberCount().getValue(),
                goalRoom.getCreatedAt(), goalRoom.getStartDate(), goalRoom.getEndDate(),
                new MemberResponse(leader.getId(), leader.getNickname().getValue()));
    }
}