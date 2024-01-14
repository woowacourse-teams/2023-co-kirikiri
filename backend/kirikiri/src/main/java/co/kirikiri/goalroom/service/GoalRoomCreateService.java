package co.kirikiri.goalroom.service;

import co.kirikiri.common.aop.ExceptionConvert;
import co.kirikiri.common.exception.BadRequestException;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomMember;
import co.kirikiri.goalroom.domain.GoalRoomMembers;
import co.kirikiri.goalroom.domain.GoalRoomPendingMember;
import co.kirikiri.goalroom.domain.GoalRoomPendingMembers;
import co.kirikiri.goalroom.domain.GoalRoomRoadmapNode;
import co.kirikiri.goalroom.domain.GoalRoomRoadmapNodes;
import co.kirikiri.goalroom.domain.GoalRoomRole;
import co.kirikiri.goalroom.domain.vo.Period;
import co.kirikiri.goalroom.persistence.GoalRoomMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomPendingMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomRepository;
import co.kirikiri.goalroom.service.dto.GoalRoomCreateDto;
import co.kirikiri.goalroom.service.dto.GoalRoomRoadmapNodeDto;
import co.kirikiri.goalroom.service.dto.request.GoalRoomCreateRequest;
import co.kirikiri.goalroom.service.event.GoalRoomLeaderUpdateEvent;
import co.kirikiri.goalroom.service.mapper.GoalRoomMapper;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@ExceptionConvert
public class GoalRoomCreateService {

    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final RoadmapContentRepository roadmapContentRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Long create(final GoalRoomCreateRequest goalRoomCreateRequest, final String memberIdentifier) {
        final GoalRoomCreateDto goalRoomCreateDto = GoalRoomMapper.convertToGoalRoomCreateDto(goalRoomCreateRequest);
        final RoadmapContent roadmapContent = findRoadmapContentById(goalRoomCreateDto.roadmapContentId());
        validateDeletedRoadmap(roadmapContent);
        validateNodeSizeEqual(roadmapContent.nodesSize(), goalRoomCreateDto.goalRoomRoadmapNodeDtosSize());
        final GoalRoom goalRoom = createGoalRoom(goalRoomCreateDto, roadmapContent);
        final GoalRoom savedGoalRoom = goalRoomRepository.save(goalRoom);

        applicationEventPublisher.publishEvent(new GoalRoomLeaderUpdateEvent(savedGoalRoom.getId(), memberIdentifier));

        return savedGoalRoom.getId();
    }

    private RoadmapContent findRoadmapContentById(final Long roadmapContentId) {
        return roadmapContentRepository.findByIdWithRoadmap(roadmapContentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 로드맵입니다."));
    }

    private void validateDeletedRoadmap(final RoadmapContent roadmapContent) {
        final Roadmap roadmap = roadmapContent.getRoadmap();
        if (roadmap.isDeleted()) {
            throw new BadRequestException("삭제된 로드맵에 대해 골룸을 생성할 수 없습니다.");
        }
    }

    private void validateNodeSizeEqual(final int roadmapNodesSize, final int goalRoomRoadmapNodeDtosSize) {
        if (roadmapNodesSize != goalRoomRoadmapNodeDtosSize) {
            throw new BadRequestException("모든 노드에 대해 기간이 설정돼야 합니다.");
        }
    }

    private GoalRoom createGoalRoom(final GoalRoomCreateDto goalRoomCreateDto, final RoadmapContent roadmapContent) {
        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = makeGoalRoomRoadmapNodes(
                goalRoomCreateDto.goalRoomRoadmapNodeDtos(), roadmapContent);
        validateGoalRoomRoadmapNodeSize(goalRoomRoadmapNodes, roadmapContent);
        return new GoalRoom(goalRoomCreateDto.goalRoomName(), goalRoomCreateDto.limitedMemberCount(),
                roadmapContent.getId(), goalRoomRoadmapNodes);
    }

    private static GoalRoomRoadmapNodes makeGoalRoomRoadmapNodes(
            final List<GoalRoomRoadmapNodeDto> goalRoomRoadmapNodeDtos,
            final RoadmapContent roadmapContent) {
        final List<GoalRoomRoadmapNode> goalRoomRoadmapNodes = goalRoomRoadmapNodeDtos.stream()
                .map(it -> makeGoalRoomRoadmapNode(roadmapContent, it))
                .toList();
        return new GoalRoomRoadmapNodes(goalRoomRoadmapNodes);
    }

    private static GoalRoomRoadmapNode makeGoalRoomRoadmapNode(final RoadmapContent roadmapContent,
                                                               final GoalRoomRoadmapNodeDto it) {
        return new GoalRoomRoadmapNode(new Period(it.startDate(), it.endDate()), it.checkCount(),
                findRoadmapNode(roadmapContent, it.roadmapNodeId()).getId());
    }

    private static RoadmapNode findRoadmapNode(final RoadmapContent roadmapContent, final Long roadmapNodeId) {
        return roadmapContent.findRoadmapNodeById(roadmapNodeId)
                .orElseThrow(() -> new NotFoundException("로드맵에 존재하지 않는 노드입니다."));
    }

    private void validateGoalRoomRoadmapNodeSize(final GoalRoomRoadmapNodes goalRoomRoadmapNodes,
                                                 final RoadmapContent roadmapContent) {
        final int goalRoomRoadmapNodesSize = goalRoomRoadmapNodes.size();
        if (goalRoomRoadmapNodesSize > roadmapContent.nodesSize()) {
            throw new BadRequestException("로드맵의 노드 수보다 골룸의 노드 수가 큽니다.");
        }
    }

    public void join(final String identifier, final Long goalRoomId) {
        final Member member = findMemberByIdentifier(identifier);
        final GoalRoom goalRoom = findGoalRoomByIdWithPessimisticLock(goalRoomId);
        validateJoinGoalRoom(goalRoom, member);
        final GoalRoomPendingMember newMember = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, goalRoom,
                member.getId());
        goalRoomPendingMemberRepository.save(newMember);
    }

    private Member findMemberByIdentifier(final String memberIdentifier) {
        return memberRepository.findByIdentifier(new Identifier(memberIdentifier))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    private GoalRoom findGoalRoomByIdWithPessimisticLock(final Long goalRoomId) {
        return goalRoomRepository.findGoalRoomByIdWithPessimisticLock(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
    }

    private void validateJoinGoalRoom(final GoalRoom goalRoom, final Member member) {
        final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(
                findGoalRoomPendingMembers(goalRoom));
        validateMemberCount(goalRoomPendingMembers, goalRoom);
        validateRecruiting(goalRoom);
        validateAlreadyParticipated(goalRoomPendingMembers, member);
    }

    private List<GoalRoomPendingMember> findGoalRoomPendingMembers(final GoalRoom goalRoom) {
        return goalRoomPendingMemberRepository.findByGoalRoom(goalRoom);
    }

    private void validateMemberCount(final GoalRoomPendingMembers goalRoomPendingMembers, final GoalRoom goalRoom) {
        if (goalRoomPendingMembers.size() >= goalRoom.getLimitedMemberCount().getValue()) {
            throw new BadRequestException("제한 인원이 꽉 찬 골룸에는 참여할 수 없습니다.");
        }
    }

    private void validateRecruiting(final GoalRoom goalRoom) {
        if (!goalRoom.isRecruiting()) {
            throw new BadRequestException("모집 중이지 않은 골룸에는 참여할 수 없습니다.");
        }
    }

    private void validateAlreadyParticipated(final GoalRoomPendingMembers goalRoomPendingMembers, final Member member) {
        if (goalRoomPendingMembers.containGoalRoomPendingMember(member.getId())) {
            throw new BadRequestException("이미 참여한 골룸에는 참여할 수 없습니다.");
        }
    }

    public void leave(final String identifier, final Long goalRoomId) {
        final Member member = findMemberByIdentifier(identifier);
        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        validateRunning(goalRoom);

        if (goalRoom.isRecruiting()) {
            leaveRecruitingGoalRoom(member, goalRoom);
        }
        if (goalRoom.isCompleted()) {
            leaveCompletedGoalRoom(member, goalRoom);
        }

        // todo: 이벤트 분리 (회원이 나가는 것과 빈 골룸이면 삭제되는 것은 다른 관심사)
//        if (goalRoom.isEmptyGoalRoom()) {
//            goalRoomRepository.delete(goalRoom);
//        }
    }

    private GoalRoom findGoalRoomById(final Long goalRoomId) {
        return goalRoomRepository.findById(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
    }

    private void validateRunning(final GoalRoom goalRoom) {
        if (goalRoom.isRunning()) {
            throw new BadRequestException("진행중인 골룸에서는 나갈 수 없습니다.");
        }
    }

    private void leaveRecruitingGoalRoom(final Member member, final GoalRoom goalRoom) {
        final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(
                findGoalRoomPendingMembers(goalRoom));
        final GoalRoomPendingMember goalRoomPendingMember = goalRoomPendingMembers.findByMemberId(member.getId())
                .orElseThrow(() -> new BadRequestException("골룸에 참여한 사용자가 아닙니다. memberId = " + member.getId()));
        goalRoomPendingMembers.changeLeaderIfLeaderLeave(goalRoomPendingMember);
        goalRoomPendingMemberRepository.delete(goalRoomPendingMember);
    }

    private void leaveCompletedGoalRoom(final Member member, final GoalRoom goalRoom) {
        final GoalRoomMembers goalRoomMembers = new GoalRoomMembers(findGoalRoomMembers(goalRoom));
        final GoalRoomMember goalRoomMember = goalRoomMembers.findByMemberId(member.getId())
                .orElseThrow(() -> new BadRequestException("골룸에 참여한 사용자가 아닙니다. memberId = " + member.getId()));
        goalRoomMembers.changeLeaderIfLeaderLeave(goalRoomMember);
        goalRoomMemberRepository.delete(goalRoomMember);
    }

    private List<GoalRoomMember> findGoalRoomMembers(final GoalRoom goalRoom) {
        return goalRoomMemberRepository.findAllByGoalRoom(goalRoom);
    }

    public void startGoalRoom(final String memberIdentifier, final Long goalRoomId) {
        final Member member = findMemberByIdentifier(memberIdentifier);

        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        checkGoalRoomStartDate(goalRoom);

        final List<GoalRoomPendingMember> findGoalRoomPendingMembers = findGoalRoomPendingMembers(goalRoom);
        checkAlreadyStarted(findGoalRoomPendingMembers);

        final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(findGoalRoomPendingMembers);
        checkGoalRoomLeader(member.getId(), goalRoomPendingMembers);

        saveGoalRoomMemberFromPendingMembers(goalRoomPendingMembers);
        goalRoom.start();
    }

    private void checkGoalRoomStartDate(final GoalRoom goalRoom) {
        if (goalRoom.cannotStart()) {
            throw new BadRequestException("골룸의 시작 날짜가 되지 않았습니다.");
        }
    }

    private void checkAlreadyStarted(final List<GoalRoomPendingMember> goalRoomPendingMembers) {
        if (goalRoomPendingMembers.isEmpty()) {
            throw new BadRequestException("이미 시작된 골룸입니다.");
        }
    }

    private void checkGoalRoomLeader(final Long memberId, final GoalRoomPendingMembers goalRoomPendingMembers) {
        if (goalRoomPendingMembers.isNotLeader(memberId)) {
            throw new BadRequestException("골룸의 리더만 골룸을 시작할 수 있습니다.");
        }
    }

    private void saveGoalRoomMemberFromPendingMembers(final GoalRoomPendingMembers goalRoomPendingMembers) {
        final List<GoalRoomMember> goalRoomMembers = makeGoalRoomMembers(goalRoomPendingMembers);
        goalRoomMemberRepository.saveAllInBatch(goalRoomMembers);
        goalRoomPendingMemberRepository.deleteAllInBatch(goalRoomPendingMembers.getValues());
    }

    private List<GoalRoomMember> makeGoalRoomMembers(final GoalRoomPendingMembers goalRoomPendingMembers) {
        return goalRoomPendingMembers.getValues().stream()
                .map(this::makeGoalRoomMember)
                .toList();
    }

    private GoalRoomMember makeGoalRoomMember(final GoalRoomPendingMember goalRoomPendingMember) {
        return new GoalRoomMember(goalRoomPendingMember.getRole(), goalRoomPendingMember.getJoinedAt(),
                goalRoomPendingMember.getGoalRoom(), goalRoomPendingMember.getMemberId());
    }
}
