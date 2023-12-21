package co.kirikiri.goalroom.service;

import co.kirikiri.common.aop.ExceptionConvert;
import co.kirikiri.common.exception.BadRequestException;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomMember;
import co.kirikiri.goalroom.domain.GoalRoomPendingMember;
import co.kirikiri.goalroom.domain.GoalRoomRoadmapNodes;
import co.kirikiri.goalroom.persistence.GoalRoomRepository;
import co.kirikiri.goalroom.service.dto.GoalRoomCreateDto;
import co.kirikiri.goalroom.service.dto.request.GoalRoomCreateRequest;
import co.kirikiri.goalroom.service.mapper.GoalRoomMapper;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@ExceptionConvert
public class GoalRoomCreateService {

    private final GoalRoomRepository goalRoomRepository;
    private final RoadmapContentRepository roadmapContentRepository;
    private final MemberRepository memberRepository;

    public Long create(final GoalRoomCreateRequest goalRoomCreateRequest, final String memberIdentifier) {
        final GoalRoomCreateDto goalRoomCreateDto = GoalRoomMapper.convertToGoalRoomCreateDto(goalRoomCreateRequest);
        final RoadmapContent roadmapContent = findRoadmapContentById(goalRoomCreateDto.roadmapContentId());
        validateDeletedRoadmap(roadmapContent);
        validateNodeSizeEqual(roadmapContent.nodesSize(), goalRoomCreateDto.goalRoomRoadmapNodeDtosSize());
        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = GoalRoomMapper.convertToGoalRoomRoadmapNodes(
                goalRoomCreateDto.goalRoomRoadmapNodeDtos(), roadmapContent);
        final Member leader = findMemberByIdentifier(memberIdentifier);

        final GoalRoom goalRoom = new GoalRoom(goalRoomCreateDto.goalRoomName(), goalRoomCreateDto.limitedMemberCount(),
                roadmapContent.getId(), leader.getId());
        validateGoalRoomRoadmapNodeSize(goalRoom, goalRoomRoadmapNodes, roadmapContent);
        goalRoom.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        return goalRoomRepository.save(goalRoom).getId();
    }

    private void validateGoalRoomRoadmapNodeSize(final GoalRoom goalRoom, final GoalRoomRoadmapNodes goalRoomRoadmapNodes,
                                                 final RoadmapContent roadmapContent) {
        final int totalSize = goalRoomRoadmapNodes.size() + goalRoom.goalRoomRoadmapNodeSize();
        if (totalSize > roadmapContent.nodesSize()) {
            throw new BadRequestException("로드맵의 노드 수보다 골룸의 노드 수가 큽니다.");
        }
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

    private Member findMemberByIdentifier(final String memberIdentifier) {
        return memberRepository.findByIdentifier(new Identifier(memberIdentifier))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    public void join(final String identifier, final Long goalRoomId) {
        final Member member = findMemberByIdentifier(identifier);
        final GoalRoom goalRoom = findGoalRoomByIdWithPessimisticLock(goalRoomId);
        goalRoom.join(member.getId());
    }

    private GoalRoom findGoalRoomByIdWithPessimisticLock(final Long goalRoomId) {
        return goalRoomRepository.findGoalRoomByIdWithPessimisticLock(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
    }

    public void leave(final String identifier, final Long goalRoomId) {
        final Member member = findMemberByIdentifier(identifier);
        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        validateStatus(goalRoom);
        goalRoom.leave(member.getId());
        if (goalRoom.isEmptyGoalRoom()) {
            goalRoomRepository.delete(goalRoom);
        }
    }

    private GoalRoom findGoalRoomById(final Long goalRoomId) {
        return goalRoomRepository.findById(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
    }

    private void validateStatus(final GoalRoom goalRoom) {
        if (goalRoom.isRunning()) {
            throw new BadRequestException("진행중인 골룸에서는 나갈 수 없습니다.");
        }
    }

    public void startGoalRoom(final String memberIdentifier, final Long goalRoomId) {
        final Member member = findMemberByIdentifier(memberIdentifier);
        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        checkGoalRoomLeader(member.getId(), goalRoom);
        validateGoalRoomStart(goalRoom);
        final List<GoalRoomPendingMember> goalRoomPendingMembers = goalRoom.getGoalRoomPendingMembers().getValues();
        saveGoalRoomMemberFromPendingMembers(goalRoomPendingMembers, goalRoom);
        goalRoom.start();
    }

    private void checkGoalRoomLeader(final Long memberId, final GoalRoom goalRoom) {
        if (goalRoom.isNotLeader(memberId)) {
            throw new BadRequestException("골룸의 리더만 골룸을 시작할 수 있습니다.");
        }
    }

    private void validateGoalRoomStart(final GoalRoom goalRoom) {
        if (goalRoom.cannotStart()) {
            throw new BadRequestException("골룸의 시작 날짜가 되지 않았습니다.");
        }
    }

    private void saveGoalRoomMemberFromPendingMembers(final List<GoalRoomPendingMember> goalRoomPendingMembers,
                                                      final GoalRoom goalRoom) {
        final List<GoalRoomMember> goalRoomMembers = makeGoalRoomMembers(goalRoomPendingMembers);
        goalRoom.addAllGoalRoomMembers(goalRoomMembers);
        goalRoom.deleteAllPendingMembers();
    }

    private List<GoalRoomMember> makeGoalRoomMembers(final List<GoalRoomPendingMember> goalRoomPendingMembers) {
        return goalRoomPendingMembers.stream()
                .map(this::makeGoalRoomMember)
                .toList();
    }

    private GoalRoomMember makeGoalRoomMember(final GoalRoomPendingMember goalRoomPendingMember) {
        return new GoalRoomMember(goalRoomPendingMember.getRole(), goalRoomPendingMember.getJoinedAt(),
                goalRoomPendingMember.getGoalRoom(), goalRoomPendingMember.getMemberId());
    }
}
