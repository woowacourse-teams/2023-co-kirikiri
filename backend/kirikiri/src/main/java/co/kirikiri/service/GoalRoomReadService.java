package co.kirikiri.service;

import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.goalroom.GoalRoomToDos;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.ForbiddenException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.goalroom.CheckFeedRepository;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.goalroom.GoalRoomToDoCheckRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.service.dto.goalroom.request.GoalRoomStatusTypeRequest;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomMemberResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomRoadmapNodeResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomTodoResponse;
import co.kirikiri.service.dto.member.response.MemberGoalRoomForListResponse;
import co.kirikiri.service.dto.member.response.MemberGoalRoomResponse;
import co.kirikiri.service.mapper.GoalRoomMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoalRoomReadService {

    private final MemberRepository memberRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final GoalRoomToDoCheckRepository goalRoomToDoCheckRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;
    private final CheckFeedRepository checkFeedRepository;

    public GoalRoomResponse findGoalRoom(final Long goalRoomId) {
        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        return GoalRoomMapper.convertGoalRoomResponse(goalRoom);
    }

    private GoalRoom findGoalRoomById(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithRoadmapContent(goalRoomId)
                .orElseThrow(() -> new NotFoundException("골룸 정보가 존재하지 않습니다. goalRoomId = " + goalRoomId));
    }

    public GoalRoomCertifiedResponse findGoalRoom(final String identifier, final Long goalRoomId) {
        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        final boolean isJoined = isMemberGoalRoomJoin(new Identifier(identifier), goalRoom);
        return GoalRoomMapper.convertGoalRoomCertifiedResponse(goalRoom, isJoined);
    }

    private boolean isMemberGoalRoomJoin(final Identifier identifier, final GoalRoom goalRoom) {
        if (goalRoom.isRecruiting()) {
            return goalRoomPendingMemberRepository.findByGoalRoomAndMemberIdentifier(goalRoom, identifier).isPresent();
        }
        return goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(goalRoom, identifier).isPresent();
    }

    public List<GoalRoomMemberResponse> findGoalRoomMembers(final Long goalRoomId) {
        final List<GoalRoomMember> goalRoomMembers = goalRoomMemberRepository.findByGoalRoomIdOrderByAccomplishmentRateDesc(
                goalRoomId);
        checkGoalRoomEmpty(goalRoomId, goalRoomMembers);
        return GoalRoomMapper.convertToGoalRoomMemberResponses(goalRoomMembers);
    }

    private void checkGoalRoomEmpty(final Long goalRoomId, final List<GoalRoomMember> goalRoomMembers) {
        if (goalRoomMembers.isEmpty()) {
            throw new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId);
        }
    }

    public List<GoalRoomTodoResponse> findAllGoalRoomTodo(final Long goalRoomId, final String identifier) {
        validateGoalRoomMember(goalRoomId, identifier);
        final GoalRoomToDos goalRoomToDos = findGoalRoomTodosByGoalRoomId(goalRoomId);
        final List<Long> checkedTodoIds = findMemberCheckedGoalRoomToDoIds(goalRoomId, identifier);
        return GoalRoomMapper.convertGoalRoomTodoResponses(goalRoomToDos, checkedTodoIds);
    }

    private void validateGoalRoomMember(final Long goalRoomId, final String identifier) {
        if (goalRoomRepository.findGoalRoomMember(goalRoomId, new Identifier(identifier)).isEmpty()) {
            throw new ForbiddenException(
                    "골룸에 참여하지 않은 사용자입니다. goalRoomId = " + goalRoomId + " memberIdentifier = " + identifier);
        }
    }

    private GoalRoomToDos findGoalRoomTodosByGoalRoomId(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithTodos(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId))
                .getGoalRoomToDos();
    }

    private List<Long> findMemberCheckedGoalRoomToDoIds(final Long goalRoomId, final String identifier) {
        return goalRoomToDoCheckRepository.findByGoalRoomIdAndMemberIdentifier(
                        goalRoomId, new Identifier(identifier)).stream()
                .map(goalRoomToDoCheck -> goalRoomToDoCheck.getGoalRoomToDo().getId())
                .toList();
    }

    public MemberGoalRoomResponse findMemberGoalRoom(final String identifier, final Long goalRoomId) {
        final GoalRoom goalRoom = findMemberGoalRoomById(goalRoomId);
        final Member member = findMemberByIdentifier(new Identifier(identifier));
        validateMemberInGoalRoom(goalRoom, member);

        final GoalRoomRoadmapNode currentGoalRoomRoadmapNode = findCurrentGoalRoomNode(goalRoom);
        final List<CheckFeed> checkFeeds = checkFeedRepository.findByGoalRoomRoadmapNode(currentGoalRoomRoadmapNode);
        final List<Long> checkedTodoIds = findMemberCheckedGoalRoomToDoIds(goalRoomId, identifier);
        return GoalRoomMapper.convertToMemberGoalRoomResponse(goalRoom, checkFeeds, checkedTodoIds);
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

    private GoalRoomRoadmapNode findCurrentGoalRoomNode(final GoalRoom goalRoom) {
        return goalRoom.getNodeByDate(LocalDate.now())
                .orElse(null);
    }

    public List<MemberGoalRoomForListResponse> findMemberGoalRooms(final String identifier) {
        final Member member = findMemberByIdentifier(new Identifier(identifier));
        final List<GoalRoom> memberGoalRooms = goalRoomRepository.findByMember(member);
        return GoalRoomMapper.convertToMemberGoalRoomForListResponses(memberGoalRooms);
    }

    public List<MemberGoalRoomForListResponse> findMemberGoalRoomsByStatusType(final String identifier,
                                                                               final GoalRoomStatusTypeRequest goalRoomStatusTypeRequest) {
        final Member member = findMemberByIdentifier(new Identifier(identifier));
        final GoalRoomStatus goalRoomStatus = GoalRoomMapper.convertToGoalRoomStatus(goalRoomStatusTypeRequest);
        final List<GoalRoom> memberGoalRooms = goalRoomRepository.findByMemberAndStatus(member, goalRoomStatus);
        return GoalRoomMapper.convertToMemberGoalRoomForListResponses(memberGoalRooms);
    }

    public List<GoalRoomRoadmapNodeResponse> findAllGoalRoomNodes(final Long goalRoomId, final String identifier) {
        final GoalRoomRoadmapNodes goalRoomNodes = findGoalRoomNodesByGoalRoomId(goalRoomId);
        validateGoalRoomMember(goalRoomId, identifier);
        return GoalRoomMapper.convertGoalRoomNodeResponses(goalRoomNodes);
    }

    private GoalRoomRoadmapNodes findGoalRoomNodesByGoalRoomId(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithNodes(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId))
                .getGoalRoomRoadmapNodes();
    }

    public List<GoalRoomCheckFeedResponse> findGoalRoomCheckFeeds(final String identifier, final Long goalRoomId) {
        final GoalRoom goalRoom = findGoalRoomWithNodesById(goalRoomId);
        validateJoinedMemberInRunningGoalRoom(goalRoom, identifier);
        final boolean canGetCheckFeed = goalRoom.getNodeByDate(LocalDate.now()).isPresent();
        if (!canGetCheckFeed) {
            return Collections.emptyList();
        }
        final GoalRoomRoadmapNode currentGoalRoomRoadmapNode = goalRoom.getNodeByDate(LocalDate.now()).get();
        final List<CheckFeed> checkFeeds = checkFeedRepository.findByGoalRoomRoadmapNodeWithGoalRoomMemberAndMemberImage(
                currentGoalRoomRoadmapNode);
        return GoalRoomMapper.convertToGoalRoomCheckFeedResponse(checkFeeds);
    }

    private GoalRoom findGoalRoomWithNodesById(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithNodes(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
    }

    private void validateJoinedMemberInRunningGoalRoom(final GoalRoom goalRoom, final String identifier) {
        goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(goalRoom, new Identifier(identifier))
                .orElseThrow(() -> new BadRequestException("골룸에 참여하지 않은 회원입니다."));
    }
}
