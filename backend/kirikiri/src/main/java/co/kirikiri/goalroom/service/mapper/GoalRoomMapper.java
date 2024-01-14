package co.kirikiri.goalroom.service.mapper;

import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomRoadmapNode;
import co.kirikiri.goalroom.domain.GoalRoomRoadmapNodes;
import co.kirikiri.goalroom.domain.GoalRoomStatus;
import co.kirikiri.goalroom.domain.vo.GoalRoomName;
import co.kirikiri.goalroom.domain.vo.LimitedMemberCount;
import co.kirikiri.goalroom.persistence.dto.GoalRoomMemberSortType;
import co.kirikiri.goalroom.persistence.dto.RoadmapGoalRoomsOrderType;
import co.kirikiri.goalroom.service.dto.GoalRoomCreateDto;
import co.kirikiri.goalroom.service.dto.GoalRoomMemberDto;
import co.kirikiri.goalroom.service.dto.GoalRoomMemberSortTypeDto;
import co.kirikiri.goalroom.service.dto.GoalRoomRoadmapNodeDetailDto;
import co.kirikiri.goalroom.service.dto.GoalRoomRoadmapNodeDto;
import co.kirikiri.goalroom.service.dto.MemberDto;
import co.kirikiri.goalroom.service.dto.MemberGoalRoomForListDto;
import co.kirikiri.goalroom.service.dto.RoadmapGoalRoomDto;
import co.kirikiri.goalroom.service.dto.RoadmapGoalRoomScrollDto;
import co.kirikiri.goalroom.service.dto.request.GoalRoomCreateRequest;
import co.kirikiri.goalroom.service.dto.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.goalroom.service.dto.request.GoalRoomStatusTypeRequest;
import co.kirikiri.goalroom.service.dto.response.DashBoardCheckFeedResponse;
import co.kirikiri.goalroom.service.dto.response.DashBoardToDoResponse;
import co.kirikiri.goalroom.service.dto.response.GoalRoomCertifiedResponse;
import co.kirikiri.goalroom.service.dto.response.GoalRoomMemberResponse;
import co.kirikiri.goalroom.service.dto.response.GoalRoomResponse;
import co.kirikiri.goalroom.service.dto.response.GoalRoomRoadmapNodeDetailResponse;
import co.kirikiri.goalroom.service.dto.response.GoalRoomRoadmapNodeResponse;
import co.kirikiri.goalroom.service.dto.response.GoalRoomRoadmapNodesResponse;
import co.kirikiri.goalroom.service.dto.response.MemberGoalRoomForListResponse;
import co.kirikiri.goalroom.service.dto.response.MemberGoalRoomResponse;
import co.kirikiri.goalroom.service.dto.response.MemberResponse;
import co.kirikiri.goalroom.service.dto.response.RoadmapGoalRoomResponse;
import co.kirikiri.service.dto.roadmap.RoadmapGoalRoomNumberDto;
import co.kirikiri.service.dto.roadmap.RoadmapGoalRoomsOrderTypeDto;
import co.kirikiri.service.dto.roadmap.response.RoadmapGoalRoomResponses;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GoalRoomMapper {

    private static final int MAX_MEMBER_GOAL_ROOM_CHECK_FEED_NUMBER = 4;

    public static GoalRoomCreateDto convertToGoalRoomCreateDto(final GoalRoomCreateRequest goalRoomCreateRequest) {
        final List<GoalRoomRoadmapNodeRequest> goalRoomRoadmapNodeRequests = goalRoomCreateRequest.goalRoomRoadmapNodeRequests();
        final List<GoalRoomRoadmapNodeDto> goalRoomRoadmapNodeDtos = makeGoalRoomRoadmapNodeDtos(
                goalRoomRoadmapNodeRequests);

        return new GoalRoomCreateDto(
                goalRoomCreateRequest.roadmapContentId(),
                new GoalRoomName(goalRoomCreateRequest.name()),
                new LimitedMemberCount(goalRoomCreateRequest.limitedMemberCount()),
                goalRoomRoadmapNodeDtos);
    }

    private static List<GoalRoomRoadmapNodeDto> makeGoalRoomRoadmapNodeDtos(
            final List<GoalRoomRoadmapNodeRequest> goalRoomRoadmapNodeRequests) {
        return goalRoomRoadmapNodeRequests
                .stream()
                .map(it -> new GoalRoomRoadmapNodeDto(it.roadmapNodeId(), it.checkCount(), it.startDate(),
                        it.endDate()))
                .toList();
    }

    public static GoalRoomResponse convertGoalRoomResponse(final GoalRoom goalRoom, final int currentMemberCount,
                                                           final RoadmapNodes roadmapNodes) {
        final GoalRoomRoadmapNodes nodes = goalRoom.getGoalRoomRoadmapNodes();
        final List<GoalRoomRoadmapNodeResponse> roadmapNodeResponses = convertGoalRoomNodeResponses(nodes,
                roadmapNodes);
        final int period = goalRoom.calculateTotalPeriod();
        return new GoalRoomResponse(goalRoom.getName().getValue(), currentMemberCount,
                goalRoom.getLimitedMemberCount().getValue(), roadmapNodeResponses, period);
    }

    public static List<GoalRoomRoadmapNodeResponse> convertGoalRoomNodeResponses(
            final GoalRoomRoadmapNodes goalRoomRoadmapNodes,
            final RoadmapNodes roadmapNodes) {
        return goalRoomRoadmapNodes.getValues().stream()
                .map(goalRoomRoadmapNode -> convertGoalRoomNodeResponse(goalRoomRoadmapNode, roadmapNodes))
                .toList();
    }

    private static GoalRoomRoadmapNodeResponse convertGoalRoomNodeResponse(final GoalRoomRoadmapNode node,
                                                                           final RoadmapNodes roadmapNodes) {
        final RoadmapNode roadmapNode = roadmapNodes.findById(node.getRoadmapNodeId())
                .orElseThrow(() -> new NotFoundException(
                        "존재하지 않는 로드맵 노드 아이디입니다. roadmapNodeId = " + node.getRoadmapNodeId()));
        return new GoalRoomRoadmapNodeResponse(node.getId(), roadmapNode.getTitle(), node.getStartDate(),
                node.getEndDate(), node.getCheckCount());
    }

    public static List<GoalRoomRoadmapNodeDetailResponse> convertGoalRoomNodeDetailResponses(
            final List<GoalRoomRoadmapNodeDetailDto> goalRoomRoadmapNodeDetailDtos) {
        return goalRoomRoadmapNodeDetailDtos.stream()
                .map(GoalRoomMapper::convertGoalRoomNodeDetailResponse)
                .toList();
    }

    private static GoalRoomRoadmapNodeDetailResponse convertGoalRoomNodeDetailResponse(
            final GoalRoomRoadmapNodeDetailDto goalRoomRoadmapNodeDetailDto) {
        return new GoalRoomRoadmapNodeDetailResponse(goalRoomRoadmapNodeDetailDto.id(),
                goalRoomRoadmapNodeDetailDto.title(), goalRoomRoadmapNodeDetailDto.description(),
                goalRoomRoadmapNodeDetailDto.imageUrls(), goalRoomRoadmapNodeDetailDto.startDate(),
                goalRoomRoadmapNodeDetailDto.endDate(), goalRoomRoadmapNodeDetailDto.checkCount());
    }

    public static GoalRoomCertifiedResponse convertGoalRoomCertifiedResponse(final GoalRoom goalRoom,
                                                                             final int currentMemberCount,
                                                                             final RoadmapNodes roadmapNodes,
                                                                             final boolean isJoined) {
        final GoalRoomRoadmapNodes nodes = goalRoom.getGoalRoomRoadmapNodes();
        final List<GoalRoomRoadmapNodeResponse> roadmapNodeResponses = convertGoalRoomNodeResponses(nodes,
                roadmapNodes);
        final int period = goalRoom.calculateTotalPeriod();
        return new GoalRoomCertifiedResponse(goalRoom.getName().getValue(), currentMemberCount,
                goalRoom.getLimitedMemberCount().getValue(), roadmapNodeResponses, period, isJoined);
    }

    public static RoadmapGoalRoomsOrderType convertToGoalRoomOrderType(
            final RoadmapGoalRoomsOrderTypeDto orderType) {
        if (orderType == null) {
            return RoadmapGoalRoomsOrderType.LATEST;
        }
        return RoadmapGoalRoomsOrderType.valueOf(orderType.name());
    }

    public static RoadmapGoalRoomResponses convertToRoadmapGoalRoomResponses(
            final RoadmapGoalRoomScrollDto roadmapGoalRoomScrollDto) {
        final List<RoadmapGoalRoomResponse> responses = roadmapGoalRoomScrollDto.roadmapGoalRoomDtos()
                .stream()
                .map(GoalRoomMapper::convertToRoadmapGoalRoomResponse)
                .toList();
        return new RoadmapGoalRoomResponses(responses, roadmapGoalRoomScrollDto.hasNext());
    }

    private static RoadmapGoalRoomResponse convertToRoadmapGoalRoomResponse(
            final RoadmapGoalRoomDto roadmapGoalRoomDto) {
        return new RoadmapGoalRoomResponse(roadmapGoalRoomDto.goalRoomId(), roadmapGoalRoomDto.name(),
                roadmapGoalRoomDto.status(), roadmapGoalRoomDto.currentMemberCount(),
                roadmapGoalRoomDto.limitedMemberCount(),
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

    public static MemberGoalRoomResponse convertToMemberGoalRoomResponse(final GoalRoom goalRoom,
                                                                         final Long goalRoomLeaderId,
                                                                         final int currentMemberCount,
                                                                         final RoadmapNodes roadmapNodes,
                                                                         final List<DashBoardCheckFeedResponse> allCheckFeedResponses,
                                                                         final List<DashBoardToDoResponse> todoResponses) {
        final GoalRoomRoadmapNodesResponse nodeResponses = convertToGoalRoomRoadmapNodesResponse(
                goalRoom.getGoalRoomRoadmapNodes(), roadmapNodes);
        final List<DashBoardCheckFeedResponse> checkFeedResponses = makeCheckFeedToLimit(allCheckFeedResponses);

        return new MemberGoalRoomResponse(goalRoom.getName().getValue(), goalRoom.getStatus().name(),
                goalRoomLeaderId, currentMemberCount, goalRoom.getLimitedMemberCount().getValue(),
                goalRoom.getStartDate(), goalRoom.getEndDate(), goalRoom.getRoadmapContentId(), nodeResponses,
                todoResponses, checkFeedResponses);
    }

    private static GoalRoomRoadmapNodesResponse convertToGoalRoomRoadmapNodesResponse(
            final GoalRoomRoadmapNodes nodes, final RoadmapNodes roadmapNodes) {
        final GoalRoomRoadmapNode currentNode = nodes.getNodeByDate(LocalDate.now())
                .orElse(nodes.getNodeByDate(nodes.getGoalRoomStartDate()).get());

        if (!nodes.hasBackNode(currentNode)) {
            return new GoalRoomRoadmapNodesResponse(
                    nodes.hasFrontNode(currentNode),
                    nodes.hasBackNode(currentNode),
                    List.of(convertGoalRoomNodeResponse(currentNode, roadmapNodes))
            );
        }

        final GoalRoomRoadmapNode nextNode = nodes.nextNode(currentNode).get();
        return new GoalRoomRoadmapNodesResponse(nodes.hasFrontNode(currentNode), nodes.hasBackNode(nextNode),
                List.of(convertGoalRoomNodeResponse(currentNode, roadmapNodes), convertGoalRoomNodeResponse(nextNode,
                        roadmapNodes)));
    }

    private static List<DashBoardCheckFeedResponse> makeCheckFeedToLimit(
            final List<DashBoardCheckFeedResponse> checkFeedDtos) {
        return checkFeedDtos.stream()
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
                memberGoalRoomForListDto.createdAt(), memberGoalRoomForListDto.startDate(),
                memberGoalRoomForListDto.endDate(),
                new MemberResponse(memberDto.id(), memberDto.name(), memberDto.imageUrl()));
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
