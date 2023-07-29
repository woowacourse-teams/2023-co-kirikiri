package co.kirikiri.service;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomMembers;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomPendingMembers;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.service.dto.goalroom.GoalRoomCreateDto;
import co.kirikiri.service.dto.goalroom.GoalRoomRoadmapNodeDto;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.mapper.GoalRoomMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GoalRoomCreateService {

    private static final int EMPTY_GOAL_ROOM = 0;
    private static final int MIN_MEMBER_SIZE_TO_FIND_NEXT_LEADER = 1;

    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final RoadmapContentRepository roadmapContentRepository;
    private final MemberRepository memberRepository;

    public Long create(final GoalRoomCreateRequest goalRoomCreateRequest, final String memberIdentifier) {
        final GoalRoomCreateDto goalRoomCreateDto = GoalRoomMapper.convertToGoalRoomCreateDto(goalRoomCreateRequest);
        final RoadmapContent roadmapContent = findRoadmapContentById(goalRoomCreateDto.roadmapContentId());
        checkNodeSizeEqual(roadmapContent.nodesSize(), goalRoomCreateDto.goalRoomRoadmapNodeDtosSize());
        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = makeGoalRoomRoadmapNodes(
                goalRoomCreateDto.goalRoomRoadmapNodeDtos(), roadmapContent);
        final Member leader = findMemberByIdentifier(memberIdentifier);

        final GoalRoom goalRoom = new GoalRoom(goalRoomCreateDto.goalRoomName(), goalRoomCreateDto.limitedMemberCount(),
                roadmapContent, leader);
        goalRoom.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        goalRoom.addGoalRoomTodo(goalRoomCreateDto.goalRoomToDo());
        return goalRoomRepository.save(goalRoom).getId();
    }

    private RoadmapContent findRoadmapContentById(final Long roadmapContentId) {
        return roadmapContentRepository.findById(roadmapContentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 로드맵입니다."));
    }

    private void checkNodeSizeEqual(final int roadmapNodesSize, final int goalRoomRoadmapNodeDtosSize) {
        if (roadmapNodesSize != goalRoomRoadmapNodeDtosSize) {
            throw new BadRequestException("모든 노드에 대해 기간이 설정돼야 합니다.");
        }
    }

    private GoalRoomRoadmapNodes makeGoalRoomRoadmapNodes(final List<GoalRoomRoadmapNodeDto> goalRoomRoadmapNodeDtos,
                                                          final RoadmapContent roadmapContent) {
        final List<GoalRoomRoadmapNode> goalRoomRoadmapNodes = goalRoomRoadmapNodeDtos.stream()
                .map(it -> makeGoalRoomRoadmapNode(roadmapContent, it))
                .toList();
        return new GoalRoomRoadmapNodes(goalRoomRoadmapNodes);
    }

    private GoalRoomRoadmapNode makeGoalRoomRoadmapNode(final RoadmapContent roadmapContent,
                                                        final GoalRoomRoadmapNodeDto it) {
        return new GoalRoomRoadmapNode(new Period(it.startDate(), it.endDate()), it.checkCount(),
                findRoadmapNode(roadmapContent, it.roadmapNodeId()));
    }

    private RoadmapNode findRoadmapNode(final RoadmapContent roadmapContent, final Long roadmapNodeId) {
        return roadmapContent.findRoadmapNodeById(roadmapNodeId)
                .orElseThrow(() -> new NotFoundException("로드맵에 존재하지 않는 노드입니다."));
    }

    private Member findMemberByIdentifier(final String memberIdentifier) {
        return memberRepository.findByIdentifier(new Identifier(memberIdentifier))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    public void join(final String identifier, final Long goalRoomId) {
        final Member member = findMemberByIdentifier(identifier);
        final GoalRoom goalRoom = findById(goalRoomId);
        goalRoom.join(member);
    }

    private GoalRoom findById(final Long goalRoomId) {
        return goalRoomRepository.findById(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
    }

    public void leave(final String identifier, final Long goalRoomId) {
        final Member member = findMemberByIdentifier(identifier);
        final GoalRoom goalRoom = findById(goalRoomId);
        validateStatus(goalRoom);
        if (goalRoom.isRecruiting()) {
            leaveWhenRecruiting(member, goalRoom);
            return;
        }
        leaveWhenCompleted(member, goalRoom);
    }

    private void validateStatus(final GoalRoom goalRoom) {
        if (goalRoom.isRunning()) {
            throw new BadRequestException("진행중인 골룸에서는 나갈 수 없습니다.");
        }
    }

    private void leaveWhenRecruiting(final Member member, final GoalRoom goalRoom) {
        final GoalRoomPendingMembers goalRoomPendingMembers = findGoalRoomPendingMembers(goalRoom);
        final GoalRoomPendingMember goalRoomPendingMember = goalRoomPendingMembers.findByMember(member);
        changeRoleIfLeaderLeave(goalRoomPendingMembers, goalRoomPendingMember);
        goalRoomPendingMembers.remove(goalRoomPendingMember);
        goalRoomPendingMemberRepository.delete(goalRoomPendingMember);
        removeIfEmptyRoom(goalRoom, goalRoomPendingMembers.size());
    }

    private GoalRoomPendingMembers findGoalRoomPendingMembers(final GoalRoom goalRoom) {
        return new GoalRoomPendingMembers(goalRoomPendingMemberRepository.findByGoalRoom(goalRoom));
    }

    private void leaveWhenCompleted(final Member member, final GoalRoom goalRoom) {
        final GoalRoomMembers goalRoomMembers = findGoalRoomMembers(goalRoom);
        final GoalRoomMember goalRoomMember = goalRoomMembers.findByMember(member);
        changeRoleIfLeaderLeave(goalRoomMembers, goalRoomMember);
        goalRoomMembers.remove(goalRoomMember);
        goalRoomMemberRepository.delete(goalRoomMember);
        removeIfEmptyRoom(goalRoom, goalRoomMembers.size());
    }

    private GoalRoomMembers findGoalRoomMembers(final GoalRoom goalRoom) {
        return new GoalRoomMembers(goalRoomMemberRepository.findByGoalRoom(goalRoom));
    }

    private void removeIfEmptyRoom(final GoalRoom goalRoom, final int size) {
        if (size == EMPTY_GOAL_ROOM) {
            goalRoomRepository.delete(goalRoom);
        }
    }

    private void changeRoleIfLeaderLeave(final GoalRoomPendingMembers goalRoomPendingMembers,
                                         final GoalRoomPendingMember goalRoomPendingMember) {
        if (goalRoomPendingMember.isLeader() && goalRoomPendingMembers.size() > MIN_MEMBER_SIZE_TO_FIND_NEXT_LEADER) {
            goalRoomPendingMembers.findNextLeader()
                    .becomeLeader();
        }
    }

    private void changeRoleIfLeaderLeave(final GoalRoomMembers goalRoomMembers,
                                         final GoalRoomMember goalRoomMember) {
        if (goalRoomMember.isLeader() && goalRoomMembers.size() > MIN_MEMBER_SIZE_TO_FIND_NEXT_LEADER) {
            goalRoomMembers.findNextLeader()
                    .becomeLeader();
        }
    }
}
