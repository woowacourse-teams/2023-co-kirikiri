package co.kirikiri.todo.service;

import co.kirikiri.common.aop.ExceptionConvert;
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomMember;
import co.kirikiri.goalroom.persistence.GoalRoomMemberRepository;
import co.kirikiri.goalroom.service.DashBoardToDoService;
import co.kirikiri.goalroom.service.dto.response.DashBoardToDoResponse;
import co.kirikiri.todo.domain.GoalRoomToDo;
import co.kirikiri.todo.domain.GoalRoomToDoCheck;
import co.kirikiri.todo.domain.GoalRoomToDos;
import co.kirikiri.todo.persistence.GoalRoomToDoCheckRepository;
import co.kirikiri.todo.persistence.GoalRoomToDoRepository;
import co.kirikiri.todo.service.mapper.GoalRoomToDoMapper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@ExceptionConvert
public class DashBoardToDoServiceImpl implements DashBoardToDoService {

    private final GoalRoomToDoCheckRepository goalRoomToDoCheckRepository;
    private final GoalRoomToDoRepository goalRoomToDoRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DashBoardToDoResponse> findMemberCheckedGoalRoomToDoIds(final GoalRoom goalRoom,
                                                                        final Long memberId) {
        final Optional<GoalRoomMember> goalRoomMember = goalRoomMemberRepository.findByGoalRoomAndMemberId(goalRoom,
                memberId);
        if (goalRoomMember.isEmpty()) {
            return Collections.emptyList();
        }
        final List<GoalRoomToDoCheck> goalRoomToDoChecks = findByGoalRoomIdAndMemberIdentifier(goalRoom.getId(),
                goalRoomMember.get().getId());
        final List<GoalRoomToDo> goalRoomTodos = goalRoomToDoRepository.findGoalRoomToDosByGoalRoomId(goalRoom.getId());
        return GoalRoomToDoMapper.convertToDashBoardTodoResponsesLimit(new GoalRoomToDos(goalRoomTodos),
                goalRoomToDoChecks);
    }

    private List<GoalRoomToDoCheck> findByGoalRoomIdAndMemberIdentifier(final Long goalRoomId,
                                                                        final Long goalRoomMemberId) {
        return goalRoomToDoCheckRepository.findByGoalRoomIdAndGoalRoomMemberId(goalRoomId, goalRoomMemberId);
    }
}
