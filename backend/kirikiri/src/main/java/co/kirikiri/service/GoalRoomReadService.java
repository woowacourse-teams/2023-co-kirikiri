package co.kirikiri.service;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomToDos;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.exception.ForbiddenException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.goalroom.GoalRoomToDoCheckRepository;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomMemberResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomTodoResponse;
import co.kirikiri.service.mapper.GoalRoomMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoalRoomReadService {

    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final GoalRoomToDoCheckRepository goalRoomToDoCheckRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;

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

    public List<GoalRoomTodoResponse> getAllGoalRoomTodo(final Long goalRoomId, final String identifier) {
        validateGoalRoomMember(goalRoomId, identifier);
        final GoalRoomToDos goalRoomToDos = findGoalRoomTodosByGoalRoomId(goalRoomId);
        final List<Long> checkedTodoIds = findMemberCheckedGoalRoomToDoIds(goalRoomId, identifier);
        return GoalRoomMapper.convertGoalRoomTodoResponses(goalRoomToDos, checkedTodoIds);
    }

    private void validateGoalRoomMember(final Long goalRoomId, final String identifier) {
        if (goalRoomRepository.findGoalRoomMember(goalRoomId, new Identifier(identifier)).isEmpty()) {
            throw new ForbiddenException(
                    "골룸에 참여하지 않은 사용자입니다. goalRoomId = " + goalRoomId + "  memberIdentifier = " + identifier);
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
}
