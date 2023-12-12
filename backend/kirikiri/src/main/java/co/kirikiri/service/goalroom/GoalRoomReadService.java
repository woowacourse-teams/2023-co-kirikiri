package co.kirikiri.service.goalroom;

import co.kirikiri.common.aop.ExceptionConvert;
import co.kirikiri.common.exception.ForbiddenException;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.common.service.FileService;
import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.goalroom.GoalRoomToDoCheck;
import co.kirikiri.domain.goalroom.GoalRoomToDos;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.member.domain.Member;
import co.kirikiri.member.domain.vo.Identifier;
import co.kirikiri.member.persistence.MemberRepository;
import co.kirikiri.member.service.dto.MemberDto;
import co.kirikiri.member.service.dto.response.MemberGoalRoomForListResponse;
import co.kirikiri.member.service.dto.response.MemberGoalRoomResponse;
import co.kirikiri.persistence.goalroom.CheckFeedRepository;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.goalroom.GoalRoomToDoCheckRepository;
import co.kirikiri.persistence.goalroom.dto.GoalRoomMemberSortType;
import co.kirikiri.service.dto.goalroom.CheckFeedDto;
import co.kirikiri.service.dto.goalroom.GoalRoomCheckFeedDto;
import co.kirikiri.service.dto.goalroom.GoalRoomMemberDto;
import co.kirikiri.service.dto.goalroom.GoalRoomMemberSortTypeDto;
import co.kirikiri.service.dto.goalroom.GoalRoomRoadmapNodeDetailDto;
import co.kirikiri.service.dto.goalroom.MemberGoalRoomForListDto;
import co.kirikiri.service.dto.goalroom.request.GoalRoomStatusTypeRequest;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomMemberResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodeDetailResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomTodoResponse;
import co.kirikiri.service.mapper.GoalRoomMapper;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@ExceptionConvert
public class GoalRoomReadService {

    private final MemberRepository memberRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final GoalRoomToDoCheckRepository goalRoomToDoCheckRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;
    private final CheckFeedRepository checkFeedRepository;
    private final FileService fileService;

    public GoalRoomResponse findGoalRoom(final Long goalRoomId) {
        final GoalRoom goalRoom = findGoalRoomWithRoadmapContentById(goalRoomId);
        return GoalRoomMapper.convertGoalRoomResponse(goalRoom);
    }

    private GoalRoom findGoalRoomWithRoadmapContentById(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithRoadmapContent(goalRoomId)
                .orElseThrow(() -> new NotFoundException("골룸 정보가 존재하지 않습니다. goalRoomId = " + goalRoomId));
    }

    public GoalRoomCertifiedResponse findGoalRoom(final String identifier, final Long goalRoomId) {
        final GoalRoom goalRoom = findGoalRoomWithRoadmapContentById(goalRoomId);
        final boolean isJoined = isMemberGoalRoomJoin(new Identifier(identifier), goalRoom);
        return GoalRoomMapper.convertGoalRoomCertifiedResponse(goalRoom, isJoined);
    }

    private boolean isMemberGoalRoomJoin(final Identifier identifier, final GoalRoom goalRoom) {
        if (goalRoom.isRecruiting()) {
            return goalRoomPendingMemberRepository.findByGoalRoomAndMemberIdentifier(goalRoom, identifier).isPresent();
        }
        return goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(goalRoom, identifier).isPresent();
    }

    public List<GoalRoomMemberResponse> findGoalRoomMembers(final Long goalRoomId,
                                                            final GoalRoomMemberSortTypeDto sortType) {
        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        final GoalRoomMemberSortType goalRoomMemberSortType = GoalRoomMapper.convertGoalRoomMemberSortType(sortType);
        if (goalRoom.isRecruiting()) {
            final List<GoalRoomPendingMember> goalRoomPendingMembers = goalRoomPendingMemberRepository.findByGoalRoomIdOrderedBySortType(
                    goalRoomId, goalRoomMemberSortType);
            final List<GoalRoomMemberDto> goalRoomMemberDtos = makeGoalRoomMemberDtosWithAccomplishmentRateZero(
                    goalRoomPendingMembers);
            return GoalRoomMapper.convertToGoalRoomMemberResponses(goalRoomMemberDtos);
        }
        final List<GoalRoomMember> goalRoomMembers = goalRoomMemberRepository.findByGoalRoomIdOrderedBySortType(
                goalRoomId, goalRoomMemberSortType);
        final List<GoalRoomMemberDto> goalRoomMemberDtos = makeGoalRoomMemberDtos(goalRoomMembers);
        return GoalRoomMapper.convertToGoalRoomMemberResponses(goalRoomMemberDtos);
    }

    private GoalRoom findGoalRoomById(final Long goalRoomId) {
        return goalRoomRepository.findById(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
    }

    private List<GoalRoomMemberDto> makeGoalRoomMemberDtosWithAccomplishmentRateZero(
            final List<GoalRoomPendingMember> goalRoomPendingMembers) {
        return goalRoomPendingMembers.stream()
                .map(this::makeGoalRoomMemberDtoWithAccomplishmentRateZero)
                .toList();
    }

    private GoalRoomMemberDto makeGoalRoomMemberDtoWithAccomplishmentRateZero(
            final GoalRoomPendingMember goalRoomPendingMember) {
        final Member member = goalRoomPendingMember.getMember();
        final URL memberImageUrl = fileService.generateUrl(member.getImage().getServerFilePath(), HttpMethod.GET);
        return new GoalRoomMemberDto(member.getId(), member.getNickname().getValue(),
                memberImageUrl.toExternalForm(), 0D);
    }

    private List<GoalRoomMemberDto> makeGoalRoomMemberDtos(
            final List<GoalRoomMember> goalRoomPendingMembers) {
        return goalRoomPendingMembers.stream()
                .map(this::makeGoalRoomMemberDto)
                .toList();
    }

    private GoalRoomMemberDto makeGoalRoomMemberDto(final GoalRoomMember goalRoomMember) {
        final Member member = goalRoomMember.getMember();
        final URL memberImageUrl = fileService.generateUrl(member.getImage().getServerFilePath(), HttpMethod.GET);
        return new GoalRoomMemberDto(member.getId(), member.getNickname().getValue(),
                memberImageUrl.toExternalForm(), goalRoomMember.getAccomplishmentRate());
    }

    public List<GoalRoomTodoResponse> findAllGoalRoomTodo(final Long goalRoomId, final String identifier) {
        final GoalRoomToDos goalRoomToDos = findGoalRoomTodosByGoalRoomId(goalRoomId);
        validateGoalRoomMember(goalRoomId, identifier);
        final List<GoalRoomToDoCheck> checkedTodos = findMemberCheckedGoalRoomToDoIds(goalRoomId, identifier);
        return GoalRoomMapper.convertGoalRoomTodoResponses(goalRoomToDos, checkedTodos);
    }

    private void validateGoalRoomMember(final Long goalRoomId, final String identifier) {
        if (goalRoomMemberRepository.findGoalRoomMember(goalRoomId, new Identifier(identifier)).isEmpty()) {
            throw new ForbiddenException(
                    "골룸에 참여하지 않은 사용자입니다. goalRoomId = " + goalRoomId + " memberIdentifier = " + identifier);
        }
    }

    private GoalRoomToDos findGoalRoomTodosByGoalRoomId(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithTodos(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId))
                .getGoalRoomToDos();
    }

    private List<GoalRoomToDoCheck> findMemberCheckedGoalRoomToDoIds(final Long goalRoomId, final String identifier) {
        return goalRoomToDoCheckRepository.findByGoalRoomIdAndMemberIdentifier(goalRoomId, new Identifier(identifier));
    }

    public MemberGoalRoomResponse findMemberGoalRoom(final String identifier, final Long goalRoomId) {
        final GoalRoom goalRoom = findMemberGoalRoomById(goalRoomId);
        final Member member = findMemberByIdentifier(new Identifier(identifier));
        validateMemberInGoalRoom(goalRoom, member);

        final Optional<GoalRoomRoadmapNode> currentGoalRoomRoadmapNode = findCurrentGoalRoomNode(goalRoom);
        final List<CheckFeed> checkFeeds = findCheckFeedsByNodeAndGoalRoomStatus(goalRoom, currentGoalRoomRoadmapNode);
        final List<GoalRoomToDoCheck> checkedTodos = findMemberCheckedGoalRoomToDoIds(goalRoomId, identifier);
        final List<CheckFeedDto> checkFeedDtos = makeCheckFeedDtos(checkFeeds);
        return GoalRoomMapper.convertToMemberGoalRoomResponse(goalRoom, checkFeedDtos, checkedTodos);
    }

    private List<CheckFeedDto> makeCheckFeedDtos(final List<CheckFeed> checkFeeds) {
        return checkFeeds.stream()
                .map(this::makeCheckFeedDto)
                .toList();
    }

    private CheckFeedDto makeCheckFeedDto(final CheckFeed checkFeed) {
        final URL checkFeedImageUrl = fileService.generateUrl(checkFeed.getServerFilePath(), HttpMethod.GET);
        return new CheckFeedDto(checkFeed.getId(), checkFeedImageUrl.toExternalForm(),
                checkFeed.getDescription(), checkFeed.getCreatedAt());
    }

    private GoalRoom findMemberGoalRoomById(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithContentAndTodos(goalRoomId)
                .orElseThrow(() -> new NotFoundException("골룸 정보가 존재하지 않습니다. goalRoomId = " + goalRoomId));
    }

    private Member findMemberByIdentifier(final Identifier identifier) {
        return memberRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    private void validateMemberInGoalRoom(final GoalRoom goalRoom, final Member member) {
        if (!goalRoom.isGoalRoomMember(member)) {
            throw new ForbiddenException("해당 골룸에 참여하지 않은 사용자입니다.");
        }
    }

    private Optional<GoalRoomRoadmapNode> findCurrentGoalRoomNode(final GoalRoom goalRoom) {
        return goalRoom.findNodeByDate(LocalDate.now());
    }

    private List<CheckFeed> findCheckFeedsByNodeAndGoalRoomStatus(final GoalRoom goalRoom,
                                                                  final Optional<GoalRoomRoadmapNode> currentGoalRoomRoadmapNode) {
        if (goalRoom.isCompleted()) {
            return checkFeedRepository.findByGoalRoom(goalRoom);
        }
        if (goalRoom.isRunning() && currentGoalRoomRoadmapNode.isPresent()) {
            return checkFeedRepository.findByRunningGoalRoomRoadmapNode(currentGoalRoomRoadmapNode.get());
        }
        return Collections.emptyList();
    }

    public List<MemberGoalRoomForListResponse> findMemberGoalRooms(final String identifier) {
        final Member member = findMemberByIdentifier(new Identifier(identifier));
        final List<GoalRoom> memberGoalRooms = goalRoomRepository.findByMember(member);
        final List<MemberGoalRoomForListDto> memberGoalRoomForListDtos = makeMemberGoalRoomForListDto(
                memberGoalRooms);
        return GoalRoomMapper.convertToMemberGoalRoomForListResponses(memberGoalRoomForListDtos);
    }

    private List<MemberGoalRoomForListDto> makeMemberGoalRoomForListDto(final List<GoalRoom> memberGoalRooms) {
        return memberGoalRooms.stream()
                .map(this::makeMemberGoalRoomForListDto)
                .toList();
    }

    private MemberGoalRoomForListDto makeMemberGoalRoomForListDto(final GoalRoom goalRoom) {
        final Member leader = goalRoom.findGoalRoomLeader();
        final URL leaderImageUrl = fileService.generateUrl(leader.getImage().getServerFilePath(), HttpMethod.GET);
        return new MemberGoalRoomForListDto(goalRoom.getId(), goalRoom.getName().getValue(),
                goalRoom.getStatus().name(), goalRoom.getCurrentMemberCount(),
                goalRoom.getLimitedMemberCount().getValue(),
                goalRoom.getCreatedAt(), goalRoom.getStartDate(), goalRoom.getEndDate(),
                new MemberDto(leader.getId(), leader.getNickname().getValue(), leaderImageUrl.toExternalForm()));
    }

    public List<MemberGoalRoomForListResponse> findMemberGoalRoomsByStatusType(final String identifier,
                                                                               final GoalRoomStatusTypeRequest goalRoomStatusTypeRequest) {
        final Member member = findMemberByIdentifier(new Identifier(identifier));
        final GoalRoomStatus goalRoomStatus = GoalRoomMapper.convertToGoalRoomStatus(goalRoomStatusTypeRequest);
        final List<GoalRoom> memberGoalRooms = goalRoomRepository.findByMemberAndStatus(member, goalRoomStatus);
        final List<MemberGoalRoomForListDto> memberGoalRoomForListDtos = makeMemberGoalRoomForListDto(
                memberGoalRooms);
        return GoalRoomMapper.convertToMemberGoalRoomForListResponses(memberGoalRoomForListDtos);
    }

    public List<GoalRoomRoadmapNodeDetailResponse> findAllGoalRoomNodes(final Long goalRoomId,
                                                                        final String identifier) {
        final GoalRoom goalRoom = findGoalRoomWithNodesByGoalRoomId(goalRoomId);
        final GoalRoomRoadmapNodes goalRoomNodes = goalRoom.getGoalRoomRoadmapNodes();
        validateGoalRoomMember(goalRoomId, identifier);
        final List<GoalRoomRoadmapNodeDetailDto> goalRoomRoadmapNodeDetailDtos = makeGoalRoomNodeDetailDtos(
                goalRoomNodes);

        return GoalRoomMapper.convertGoalRoomNodeDetailResponses(goalRoomRoadmapNodeDetailDtos);
    }

    private GoalRoom findGoalRoomWithNodesByGoalRoomId(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithNodes(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
    }

    public List<GoalRoomRoadmapNodeDetailDto> makeGoalRoomNodeDetailDtos(
            final GoalRoomRoadmapNodes nodes) {
        return nodes.getValues().stream()
                .map(this::makeGoalRoomNodeDetailResponse)
                .toList();
    }

    private GoalRoomRoadmapNodeDetailDto makeGoalRoomNodeDetailResponse(final GoalRoomRoadmapNode node) {
        final RoadmapNode roadmapNode = node.getRoadmapNode();
        return new GoalRoomRoadmapNodeDetailDto(node.getId(), roadmapNode.getTitle(), roadmapNode.getContent(),
                makeRoadmapNodeImageUrls(roadmapNode), node.getStartDate(), node.getEndDate(), node.getCheckCount());
    }

    private List<String> makeRoadmapNodeImageUrls(final RoadmapNode roadmapNode) {
        return roadmapNode.getRoadmapNodeImages()
                .getValues()
                .stream()
                .map(it -> fileService.generateUrl(it.getServerFilePath(), HttpMethod.GET).toExternalForm())
                .toList();
    }

    public List<GoalRoomCheckFeedResponse> findGoalRoomCheckFeeds(final String identifier, final Long goalRoomId) {
        final GoalRoom goalRoom = findGoalRoomWithNodesById(goalRoomId);
        validateJoinedMemberInRunningGoalRoom(goalRoom, identifier);
        final Optional<GoalRoomRoadmapNode> currentGoalRoomRoadmapNode = findCurrentGoalRoomNode(goalRoom);
        final List<CheckFeed> checkFeeds = findCheckFeedsByNodeAndGoalRoomStatusWithMember(goalRoom,
                currentGoalRoomRoadmapNode);
        final List<GoalRoomCheckFeedDto> goalRoomCheckFeedDtos = makeGoalRoomCheckFeedDtos(checkFeeds);
        return GoalRoomMapper.convertToGoalRoomCheckFeedResponses(goalRoomCheckFeedDtos);
    }

    private GoalRoom findGoalRoomWithNodesById(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithNodes(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
    }

    private void validateJoinedMemberInRunningGoalRoom(final GoalRoom goalRoom, final String identifier) {
        if (goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(goalRoom, new Identifier(identifier))
                .isEmpty()) {
            throw new ForbiddenException("골룸에 참여하지 않은 회원입니다.");
        }
    }

    private List<CheckFeed> findCheckFeedsByNodeAndGoalRoomStatusWithMember(final GoalRoom goalRoom,
                                                                            final Optional<GoalRoomRoadmapNode> currentGoalRoomRoadmapNode) {
        if (goalRoom.isCompleted()) {
            return checkFeedRepository.findByGoalRoomWithMemberAndMemberImage(goalRoom);
        }
        if (goalRoom.isRunning() && currentGoalRoomRoadmapNode.isPresent()) {
            return checkFeedRepository.findByRunningGoalRoomRoadmapNodeWithMemberAndMemberImage(
                    currentGoalRoomRoadmapNode.get());
        }
        return Collections.emptyList();
    }

    public List<GoalRoomCheckFeedDto> makeGoalRoomCheckFeedDtos(
            final List<CheckFeed> checkFeeds) {
        return checkFeeds.stream()
                .map(this::makeGoalRoomCheckFeedDto)
                .toList();
    }

    private GoalRoomCheckFeedDto makeGoalRoomCheckFeedDto(final CheckFeed checkFeed) {
        final GoalRoomMember goalRoomMember = checkFeed.getGoalRoomMember();
        final Member member = goalRoomMember.getMember();

        final URL memberImageUrl = fileService.generateUrl(member.getImage().getServerFilePath(), HttpMethod.GET);

        return new GoalRoomCheckFeedDto(new MemberDto(member.getId(), member.getNickname().getValue(),
                memberImageUrl.toExternalForm()), makeCheckFeedDto(checkFeed));
    }
}
