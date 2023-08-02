package co.kirikiri.service.mapper;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomToDo;
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
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomMemberResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.member.response.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapGoalRoomsFilterTypeDto;
import co.kirikiri.service.dto.roadmap.response.RoadmapGoalRoomResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GoalRoomMapper {

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
                goalRoom.getCurrentPendingMemberCount(),
                goalRoom.getLimitedMemberCount().getValue(), goalRoom.getCreatedAt(), goalRoom.getStartDate(),
                goalRoom.getEndDate(), convertToMemberResponse(goalRoom));
    }

    private static MemberResponse convertToMemberResponse(final GoalRoom goalRoom) {
        final Member goalRoomLeader = goalRoom.findGoalRoomLeaderInPendingMember();
        return new MemberResponse(goalRoomLeader.getId(), goalRoomLeader.getNickname().getValue());
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
}
