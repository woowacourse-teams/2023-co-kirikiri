package co.kirikiri.service.mapper;

import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
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
import co.kirikiri.persistence.goalroom.dto.RoadmapGoalRoomsFilterType;
import co.kirikiri.service.dto.goalroom.GoalRoomCreateDto;
import co.kirikiri.service.dto.goalroom.GoalRoomRoadmapNodeDto;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomStatusTypeRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.CheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomMemberResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodesResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomToDoCheckResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomTodoResponse;
import co.kirikiri.service.dto.member.response.MemberGoalRoomForListResponse;
import co.kirikiri.service.dto.member.response.MemberGoalRoomResponse;
import co.kirikiri.service.dto.member.response.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapGoalRoomsFilterTypeDto;
import co.kirikiri.service.dto.roadmap.response.RoadmapGoalRoomResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
        return new GoalRoomCreateDto(goalRoomCreateRequest.roadmapContentId(),
                new GoalRoomName(goalRoomCreateRequest.name()),
                new LimitedMemberCount(goalRoomCreateRequest.limitedMemberCount()), goalRoomToDo,
                goalRoomRoadmapNodeDtos);
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

    public static RoadmapGoalRoomsFilterType convertToGoalRoomFilterType(
            final RoadmapGoalRoomsFilterTypeDto filterType) {
        if (filterType == null) {
            return RoadmapGoalRoomsFilterType.LATEST;
        }
        return RoadmapGoalRoomsFilterType.valueOf(filterType.name());
    }

    public static List<RoadmapGoalRoomResponse> convertToRoadmapGoalRoomResponses(final List<GoalRoom> goalRooms) {
        return goalRooms.stream()
                .map(GoalRoomMapper::convertToRoadmapGoalRoomResponse)
                .toList();
    }

    private static RoadmapGoalRoomResponse convertToRoadmapGoalRoomResponse(final GoalRoom goalRoom) {
        return new RoadmapGoalRoomResponse(goalRoom.getId(), goalRoom.getName().getValue(),
                goalRoom.getCurrentMemberCount(),
                goalRoom.getLimitedMemberCount().getValue(), goalRoom.getCreatedAt(), goalRoom.getStartDate(),
                goalRoom.getEndDate(), convertToMemberResponse(goalRoom));
    }

    private static MemberResponse convertToMemberResponse(final GoalRoom goalRoom) {
        final Member goalRoomLeader = goalRoom.findGoalRoomLeader();
        return new MemberResponse(goalRoomLeader.getId(), goalRoomLeader.getNickname().getValue(),
                goalRoomLeader.getImage().getServerFilePath());
    }

    public static List<GoalRoomMemberResponse> convertToGoalRoomMemberResponses(
            final List<GoalRoomMember> goalRoomMembers) {
        return goalRoomMembers.stream()
                .map(GoalRoomMapper::convertToGoalRoomMemberResponse)
                .toList();
    }

    private static GoalRoomMemberResponse convertToGoalRoomMemberResponse(final GoalRoomMember goalRoomMember) {
        final Member member = goalRoomMember.getMember();
        return new GoalRoomMemberResponse(member.getId(), member.getNickname().getValue(),
                member.getImage().getServerFilePath(), goalRoomMember.getAccomplishmentRate());
    }

    public static List<GoalRoomTodoResponse> convertGoalRoomTodoResponses(final GoalRoomToDos goalRoomToDos,
                                                                          final List<Long> checkedTodoIds) {
        return goalRoomToDos.getValues().stream()
                .map(goalRoomToDo -> convertGoalRoomTodoResponse(checkedTodoIds, goalRoomToDo))
                .toList();
    }

    private static GoalRoomTodoResponse convertGoalRoomTodoResponse(final List<Long> checkedTodoIds,
                                                                    final GoalRoomToDo goalRoomToDo) {
        final GoalRoomToDoCheckResponse checkResponse = new GoalRoomToDoCheckResponse(
                isCheckedTodo(goalRoomToDo.getId(), checkedTodoIds));
        return new GoalRoomTodoResponse(goalRoomToDo.getId(),
                goalRoomToDo.getContent(),
                goalRoomToDo.getStartDate(), goalRoomToDo.getEndDate(),
                checkResponse);
    }

    private static boolean isCheckedTodo(final Long targetTodoId, final List<Long> checkedTodoIds) {
        return checkedTodoIds.contains(targetTodoId);
    }

    public static MemberGoalRoomResponse convertToMemberGoalRoomResponse(final GoalRoom goalRoom,
                                                                         final List<CheckFeed> checkFeeds,
                                                                         final List<Long> checkedTodoIds) {
        final GoalRoomRoadmapNodesResponse nodeResponses = convertToGoalRoomRoadmapNodesResponse(
                goalRoom.getGoalRoomRoadmapNodes());
        final List<GoalRoomTodoResponse> todoResponses = convertGoalRoomTodoResponsesLimit(goalRoom.getGoalRoomToDos(),
                checkedTodoIds);
        final List<CheckFeedResponse> checkFeedResponses = convertToCheckFeedResponses(checkFeeds);

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
            return new GoalRoomRoadmapNodesResponse(nodes.hasFrontNode(currentNode), nodes.hasBackNode(currentNode),
                    List.of(new GoalRoomRoadmapNodeResponse(currentNode.getId(),
                            currentNode.getRoadmapNode().getTitle(),
                            currentNode.getStartDate(), currentNode.getEndDate(), currentNode.getCheckCount())));
        }

        final GoalRoomRoadmapNode nextNode = nodes.getNodeByDate(currentNode.getEndDate().plusDays(1)).get();
        return new GoalRoomRoadmapNodesResponse(nodes.hasFrontNode(currentNode), nodes.hasBackNode(nextNode),
                List.of(new GoalRoomRoadmapNodeResponse(currentNode.getId(), currentNode.getRoadmapNode().getTitle(),
                                currentNode.getStartDate(), currentNode.getEndDate(), currentNode.getCheckCount()),
                        new GoalRoomRoadmapNodeResponse(nextNode.getId(), nextNode.getRoadmapNode().getTitle(),
                                nextNode.getStartDate(), nextNode.getEndDate(), nextNode.getCheckCount())));
    }

    private static List<GoalRoomTodoResponse> convertGoalRoomTodoResponsesLimit(final GoalRoomToDos goalRoomToDos,
                                                                                final List<Long> checkedTodoIds) {
        return goalRoomToDos.getValues()
                .stream()
                .map(goalRoomToDo -> convertGoalRoomTodoResponse(checkedTodoIds, goalRoomToDo))
                .limit(MAX_MEMBER_GOAL_ROOM_TODO_NUMBER)
                .toList();
    }

    private static List<CheckFeedResponse> convertToCheckFeedResponses(final List<CheckFeed> checkFeeds) {
        return checkFeeds.stream()
                .map(checkFeed -> new CheckFeedResponse(checkFeed.getId(), checkFeed.getServerFilePath(),
                        checkFeed.getDescription()))
                .limit(MAX_MEMBER_GOAL_ROOM_CHECK_FEED_NUMBER)
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
                new MemberResponse(leader.getId(), leader.getNickname().getValue(),
                        leader.getImage().getServerFilePath()));
    }
}
