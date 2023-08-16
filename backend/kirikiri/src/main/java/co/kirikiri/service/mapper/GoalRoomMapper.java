package co.kirikiri.service.mapper;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.goalroom.GoalRoomToDo;
import co.kirikiri.domain.goalroom.GoalRoomToDoCheck;
import co.kirikiri.domain.goalroom.GoalRoomToDos;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.GoalRoomTodoContent;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.persistence.goalroom.dto.GoalRoomMemberSortType;
import co.kirikiri.persistence.goalroom.dto.RoadmapGoalRoomsFilterType;
import co.kirikiri.service.dto.goalroom.CheckFeedDto;
import co.kirikiri.service.dto.goalroom.GoalRoomCheckFeedDto;
import co.kirikiri.service.dto.goalroom.GoalRoomCreateDto;
import co.kirikiri.service.dto.goalroom.GoalRoomMemberDto;
import co.kirikiri.service.dto.goalroom.GoalRoomMemberSortTypeDto;
import co.kirikiri.service.dto.goalroom.GoalRoomRoadmapNodeDto;
import co.kirikiri.service.dto.goalroom.MemberGoalRoomForListDto;
import co.kirikiri.service.dto.goalroom.RoadmapGoalRoomDto;
import co.kirikiri.service.dto.goalroom.RoadmapGoalRoomScrollDto;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomStatusTypeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.CheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomMemberResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodesResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomToDoCheckResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomTodoResponse;
import co.kirikiri.service.dto.member.MemberDto;
import co.kirikiri.service.dto.member.response.MemberGoalRoomForListResponse;
import co.kirikiri.service.dto.member.response.MemberGoalRoomResponse;
import co.kirikiri.service.dto.member.response.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapGoalRoomNumberDto;
import co.kirikiri.service.dto.roadmap.RoadmapGoalRoomsFilterTypeDto;
import co.kirikiri.service.dto.roadmap.response.RoadmapGoalRoomResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapGoalRoomResponses;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GoalRoomMapper {

    private static final int MAX_MEMBER_GOAL_ROOM_TODO_NUMBER = 3;
    private static final int MAX_MEMBER_GOAL_ROOM_CHECK_FEED_NUMBER = 4;

    public static GoalRoomCreateDto convertToGoalRoomCreateDto(final GoalRoomCreateRequest goalRoomCreateRequest) {
        final GoalRoomTodoRequest goalRoomTodoRequest = goalRoomCreateRequest.goalRoomTodo();
        final GoalRoomToDo goalRoomToDo = convertToGoalRoomTodo(goalRoomTodoRequest);
        final List<GoalRoomRoadmapNodeRequest> goalRoomRoadmapNodeRequests = goalRoomCreateRequest.goalRoomRoadmapNodeRequests();
        final List<GoalRoomRoadmapNodeDto> goalRoomRoadmapNodeDtos = makeGoalRoomRoadmapNodeDtos(
                goalRoomRoadmapNodeRequests);

        return new GoalRoomCreateDto(
                goalRoomCreateRequest.roadmapContentId(),
                new GoalRoomName(goalRoomCreateRequest.name()),
                new LimitedMemberCount(goalRoomCreateRequest.limitedMemberCount()),
                goalRoomToDo, goalRoomRoadmapNodeDtos);
    }

    public static GoalRoomToDo convertToGoalRoomTodo(final GoalRoomTodoRequest goalRoomTodoRequest) {
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
        final List<GoalRoomRoadmapNodeResponse> roadmapNodeResponses = convertGoalRoomNodeResponses(nodes);
        final int period = goalRoom.calculateTotalPeriod();
        return new GoalRoomResponse(goalRoom.getName().getValue(), goalRoom.getCurrentMemberCount(),
                goalRoom.getLimitedMemberCount().getValue(), roadmapNodeResponses, period);
    }

    public static List<GoalRoomRoadmapNodeResponse> convertGoalRoomNodeResponses(final GoalRoomRoadmapNodes nodes) {
        return nodes.getValues().stream()
                .map(GoalRoomMapper::convertGoalRoomNodeResponse)
                .toList();
    }

    private static GoalRoomRoadmapNodeResponse convertGoalRoomNodeResponse(final GoalRoomRoadmapNode node) {
        return new GoalRoomRoadmapNodeResponse(node.getId(), node.getRoadmapNode().getTitle(), node.getStartDate(),
                node.getEndDate(), node.getCheckCount());
    }

    public static GoalRoomCertifiedResponse convertGoalRoomCertifiedResponse(final GoalRoom goalRoom,
                                                                             final boolean isJoined) {
        final GoalRoomRoadmapNodes nodes = goalRoom.getGoalRoomRoadmapNodes();
        final List<GoalRoomRoadmapNodeResponse> roadmapNodeResponses = convertGoalRoomNodeResponses(nodes);
        final int period = goalRoom.calculateTotalPeriod();
        return new GoalRoomCertifiedResponse(goalRoom.getName().getValue(), goalRoom.getCurrentMemberCount(),
                goalRoom.getLimitedMemberCount().getValue(), roadmapNodeResponses, period, isJoined);
    }

    public static RoadmapGoalRoomsFilterType convertToGoalRoomFilterType(
            final RoadmapGoalRoomsFilterTypeDto filterType) {
        if (filterType == null) {
            return RoadmapGoalRoomsFilterType.LATEST;
        }
        return RoadmapGoalRoomsFilterType.valueOf(filterType.name());
    }

    public static RoadmapGoalRoomResponses convertToRoadmapGoalRoomResponses(final RoadmapGoalRoomScrollDto roadmapGoalRoomScrollDto) {
        final List<RoadmapGoalRoomResponse> responses = roadmapGoalRoomScrollDto.roadmapGoalRoomDtos()
                .stream()
                .map(GoalRoomMapper::convertToRoadmapGoalRoomResponse)
                .toList();
        return new RoadmapGoalRoomResponses(responses, roadmapGoalRoomScrollDto.hasNext());
    }

    private static RoadmapGoalRoomResponse convertToRoadmapGoalRoomResponse(final RoadmapGoalRoomDto roadmapGoalRoomDto) {
        return new RoadmapGoalRoomResponse(roadmapGoalRoomDto.goalRoomId(), roadmapGoalRoomDto.name(),
                roadmapGoalRoomDto.currentMemberCount(), roadmapGoalRoomDto.limitedMemberCount(),
                roadmapGoalRoomDto.createdAt(), roadmapGoalRoomDto.startDate(),
                roadmapGoalRoomDto.endDate(), convertToMemberResponse(roadmapGoalRoomDto.goalRoomLeader()));
    }

    private static MemberResponse convertToMemberResponse(final MemberDto memberDto) {
        return new MemberResponse(memberDto.id(), memberDto.name(), memberDto.imageUrl());
    }

    public static GoalRoomMemberSortType convertGoalRoomMemberSortType(final GoalRoomMemberSortTypeDto sortType) {
        if (sortType == null) {
            return null;
        }
        return GoalRoomMemberSortType.valueOf(sortType.name());
    }

    public static List<GoalRoomMemberResponse> convertToGoalRoomMemberResponses(
            final List<GoalRoomMemberDto> goalRoomMemberDtos) {
        return goalRoomMemberDtos.stream()
                .map(GoalRoomMapper::convertToGoalRoomMemberResponse)
                .toList();
    }

    private static GoalRoomMemberResponse convertToGoalRoomMemberResponse(final GoalRoomMemberDto goalRoomMemberDto) {
        return new GoalRoomMemberResponse(goalRoomMemberDto.memberId(), goalRoomMemberDto.nickname(),
                goalRoomMemberDto.imagePath(), goalRoomMemberDto.accomplishmentRate());
    }

    public static List<GoalRoomTodoResponse> convertGoalRoomTodoResponses(final GoalRoomToDos goalRoomToDos,
                                                                          final List<GoalRoomToDoCheck> checkedTodos) {
        return goalRoomToDos.getValues().stream()
                .map(goalRoomToDo -> convertGoalRoomTodoResponse(checkedTodos, goalRoomToDo))
                .toList();
    }

    private static GoalRoomTodoResponse convertGoalRoomTodoResponse(final List<GoalRoomToDoCheck> checkedTodos,
                                                                    final GoalRoomToDo goalRoomToDo) {
        final GoalRoomToDoCheckResponse checkResponse = new GoalRoomToDoCheckResponse(
                isCheckedTodo(goalRoomToDo.getId(), checkedTodos));
        return new GoalRoomTodoResponse(goalRoomToDo.getId(),
                goalRoomToDo.getContent(),
                goalRoomToDo.getStartDate(), goalRoomToDo.getEndDate(),
                checkResponse);
    }

    private static boolean isCheckedTodo(final Long targetTodoId, final List<GoalRoomToDoCheck> checkedTodos) {
        final List<Long> checkTodoIds = checkedTodos.stream()
                .map(goalRoomToDoCheck -> goalRoomToDoCheck.getGoalRoomToDo().getId())
                .toList();
        return checkTodoIds.contains(targetTodoId);
    }

    public static MemberGoalRoomResponse convertToMemberGoalRoomResponse(final GoalRoom goalRoom,
                                                                         final List<CheckFeedDto> checkFeedDtos,
                                                                         final List<GoalRoomToDoCheck> checkedTodos) {
        final GoalRoomRoadmapNodesResponse nodeResponses = convertToGoalRoomRoadmapNodesResponse(
                goalRoom.getGoalRoomRoadmapNodes());
        final List<GoalRoomTodoResponse> todoResponses = convertGoalRoomTodoResponsesLimit(goalRoom.getGoalRoomToDos(),
                checkedTodos);
        final List<CheckFeedResponse> checkFeedResponses = convertToCheckFeedResponses(checkFeedDtos);

        return new MemberGoalRoomResponse(goalRoom.getName().getValue(), goalRoom.getStatus().name(),
                goalRoom.findGoalRoomLeader().getId(), goalRoom.getCurrentMemberCount(),
                goalRoom.getLimitedMemberCount().getValue(), goalRoom.getStartDate(), goalRoom.getEndDate(),
                goalRoom.getRoadmapContent().getId(), nodeResponses, todoResponses, checkFeedResponses);
    }

    private static GoalRoomRoadmapNodesResponse convertToGoalRoomRoadmapNodesResponse(
            final GoalRoomRoadmapNodes nodes) {
        final GoalRoomRoadmapNode currentNode = nodes.getNodeByDate(LocalDate.now())
                .orElse(nodes.getNodeByDate(nodes.getGoalRoomStartDate()).get());

        if (!nodes.hasBackNode(currentNode)) {
            return new GoalRoomRoadmapNodesResponse(
                    nodes.hasFrontNode(currentNode),
                    nodes.hasBackNode(currentNode),
                    List.of(convertGoalRoomNodeResponse(currentNode))
            );
        }

        final GoalRoomRoadmapNode nextNode = nodes.nextNode(currentNode).get();
        return new GoalRoomRoadmapNodesResponse(nodes.hasFrontNode(currentNode), nodes.hasBackNode(nextNode),
                List.of(convertGoalRoomNodeResponse(currentNode), convertGoalRoomNodeResponse(nextNode)));
    }

    private static List<GoalRoomTodoResponse> convertGoalRoomTodoResponsesLimit(final GoalRoomToDos goalRoomToDos,
                                                                                final List<GoalRoomToDoCheck> checkedTodos) {
        return goalRoomToDos.getValues()
                .stream()
                .map(goalRoomToDo -> convertGoalRoomTodoResponse(checkedTodos, goalRoomToDo))
                .limit(MAX_MEMBER_GOAL_ROOM_TODO_NUMBER)
                .toList();
    }

    private static List<CheckFeedResponse> convertToCheckFeedResponses(final List<CheckFeedDto> checkFeedDtos) {
        return checkFeedDtos.stream()
                .map(checkFeed -> new CheckFeedResponse(checkFeed.id(), checkFeed.imageUrl(),
                        checkFeed.description(), checkFeed.createdAt()))
                .limit(MAX_MEMBER_GOAL_ROOM_CHECK_FEED_NUMBER)
                .toList();
    }

    public static GoalRoomStatus convertToGoalRoomStatus(final GoalRoomStatusTypeRequest statusType) {
        return GoalRoomStatus.valueOf(statusType.name());
    }

    public static List<MemberGoalRoomForListResponse> convertToMemberGoalRoomForListResponses(
            final List<MemberGoalRoomForListDto> memberGoalRoomForListDtos) {
        return memberGoalRoomForListDtos.stream()
                .map(GoalRoomMapper::convertToMemberGoalRoomForListResponse)
                .toList();
    }

    private static MemberGoalRoomForListResponse convertToMemberGoalRoomForListResponse(
            final MemberGoalRoomForListDto memberGoalRoomForListDto) {
        final MemberDto memberDto = memberGoalRoomForListDto.goalRoomLeader();
        return new MemberGoalRoomForListResponse(memberGoalRoomForListDto.goalRoomId(), memberGoalRoomForListDto.name(),
                memberGoalRoomForListDto.goalRoomStatus(), memberGoalRoomForListDto.currentMemberCount(),
                memberGoalRoomForListDto.limitedMemberCount(),
                memberGoalRoomForListDto.createdAt(), memberGoalRoomForListDto.startDate(), memberGoalRoomForListDto.endDate(),
                new MemberResponse(memberDto.id(), memberDto.name(),
                        memberDto.imageUrl()));
    }

    public static List<GoalRoomCheckFeedResponse> convertToGoalRoomCheckFeedResponses(
            final List<GoalRoomCheckFeedDto> checkFeeds) {
        return checkFeeds.stream()
                .map(GoalRoomMapper::convertToGoalRoomCheckFeedResponse)
                .toList();
    }

    private static GoalRoomCheckFeedResponse convertToGoalRoomCheckFeedResponse(
            final GoalRoomCheckFeedDto goalRoomCheckFeedDto) {
        final MemberDto memberDto = goalRoomCheckFeedDto.memberDto();
        final MemberResponse memberResponse = new MemberResponse(memberDto.id(), memberDto.name(),
                memberDto.imageUrl());

        final CheckFeedDto checkFeedDto = goalRoomCheckFeedDto.checkFeedDto();
        final CheckFeedResponse checkFeedResponse = new CheckFeedResponse(checkFeedDto.id(), checkFeedDto.imageUrl(),
                checkFeedDto.description(), checkFeedDto.createdAt());

        return new GoalRoomCheckFeedResponse(memberResponse, checkFeedResponse);
    }

    public static RoadmapGoalRoomNumberDto convertRoadmapGoalRoomDto(final List<GoalRoom> goalRooms) {
        final Map<GoalRoomStatus, List<GoalRoom>> goalRoomsDividedByStatus = goalRooms.stream()
                .collect(Collectors.groupingBy(GoalRoom::getStatus));
        return new RoadmapGoalRoomNumberDto(
                goalRoomsDividedByStatus.getOrDefault(GoalRoomStatus.RECRUITING, Collections.emptyList()).size(),
                goalRoomsDividedByStatus.getOrDefault(GoalRoomStatus.RUNNING, Collections.emptyList()).size(),
                goalRoomsDividedByStatus.getOrDefault(GoalRoomStatus.COMPLETED, Collections.emptyList()).size()
        );
    }
}
