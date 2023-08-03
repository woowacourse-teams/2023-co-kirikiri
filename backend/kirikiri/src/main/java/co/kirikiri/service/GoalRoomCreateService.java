package co.kirikiri.service;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomToDo;
import co.kirikiri.domain.goalroom.GoalRoomToDoCheck;
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
import co.kirikiri.persistence.goalroom.GoalRoomToDoCheckRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.service.dto.goalroom.GoalRoomCreateDto;
import co.kirikiri.service.dto.goalroom.GoalRoomRoadmapNodeDto;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.GoalRoomToDoCheckResponse;
import co.kirikiri.service.mapper.GoalRoomMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GoalRoomCreateService {

    private final MemberRepository memberRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final RoadmapContentRepository roadmapContentRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final GoalRoomToDoCheckRepository goalRoomToDoCheckRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;

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
        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        goalRoom.join(member);
    }

    private GoalRoom findGoalRoomById(final Long goalRoomId) {
        return goalRoomRepository.findById(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
    }

    @Transactional
    public Long addGoalRoomTodo(final Long goalRoomId, final String identifier,
                                final GoalRoomTodoRequest goalRoomTodoRequest) {
        final Member member = findMemberByIdentifier(identifier);
        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        checkGoalRoomCompleted(goalRoom);
        checkGoalRoomLeader(member, goalRoom);
        final GoalRoomToDo goalRoomToDo = GoalRoomMapper.convertToGoalRoomTodo(goalRoomTodoRequest);
        goalRoom.addGoalRoomTodo(goalRoomToDo);
        goalRoomRepository.save(goalRoom);
        return goalRoom.findLastGoalRoomTodo().getId();
    }

    private void checkGoalRoomCompleted(final GoalRoom goalRoom) {
        if (goalRoom.isCompleted()) {
            throw new BadRequestException("이미 종료된 골룸입니다.");
        }
    }

    private void checkGoalRoomLeader(final Member member, final GoalRoom goalRoom) {
        if (goalRoom.isNotLeader(member)) {
            throw new BadRequestException("골룸의 리더만 투드리스트를 추가할 수 있습니다.");
        }
    }

    public GoalRoomToDoCheckResponse checkGoalRoomTodo(final Long goalRoomId, final Long todoId, final String identifier) {
        final Identifier memberIdentifier = new Identifier(identifier);
        final GoalRoom goalRoom = findGoalRoomWithTodos(goalRoomId);
        final GoalRoomToDo goalRoomToDo = goalRoom.getGoalRoomToDos().findById(todoId);
        final GoalRoomMember goalRoomMember = findGoalRoomMember(memberIdentifier, goalRoom);

        final boolean isAlreadyChecked = goalRoomToDoCheckRepository.findByGoalRoomIdAndTodoIdAndMemberIdentifier(
                goalRoomId, todoId, memberIdentifier).isPresent();
        if (isAlreadyChecked) {
            goalRoomToDoCheckRepository.deleteById(todoId);
            return new GoalRoomToDoCheckResponse(false);
        }
        final GoalRoomToDoCheck goalRoomToDoCheck = new GoalRoomToDoCheck(goalRoomMember, goalRoomToDo);
        goalRoomToDoCheckRepository.save(goalRoomToDoCheck);
        return new GoalRoomToDoCheckResponse(true);
    }

    private GoalRoom findGoalRoomWithTodos(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithTodos(goalRoomId)
                .orElseThrow(() -> new NotFoundException("골룸이 존재하지 않습니다. goalRoomId = " + goalRoomId));
    }

    private GoalRoomMember findGoalRoomMember(final Identifier memberIdentifier, final GoalRoom goalRoom) {
        return goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(goalRoom,
                memberIdentifier).orElseThrow(() -> new NotFoundException(
                "골룸에 사용자가 존재하지 않습니다. goalRoomId = " + goalRoom.getId() + " memberIdentifier = "
                        + memberIdentifier.getValue()));
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void startGoalRooms() {
        final List<GoalRoom> goalRoomsToStart = goalRoomRepository.findAllByStartDateNow();
        for (final GoalRoom goalRoom : goalRoomsToStart) {
            final List<GoalRoomPendingMember> goalRoomPendingMembers = goalRoomPendingMemberRepository.findAllByGoalRoom(
                    goalRoom);
            final List<GoalRoomMember> goalRoomMembers = makeGoalRoomMembers(goalRoomPendingMembers);
            goalRoomMemberRepository.saveAll(goalRoomMembers);
            goalRoomPendingMemberRepository.deleteAll(goalRoomPendingMembers);
            goalRoom.start();
        }
    }

    private List<GoalRoomMember> makeGoalRoomMembers(final List<GoalRoomPendingMember> goalRoomPendingMembers) {
        return goalRoomPendingMembers.stream()
                .map(this::makeGoalRoomMember)
                .toList();
    }

    private GoalRoomMember makeGoalRoomMember(final GoalRoomPendingMember goalRoomPendingMember) {
        return new GoalRoomMember(goalRoomPendingMember.getRole(),
                goalRoomPendingMember.getJoinedAt(), goalRoomPendingMember.getGoalRoom(),
                goalRoomPendingMember.getMember());
    }
}
