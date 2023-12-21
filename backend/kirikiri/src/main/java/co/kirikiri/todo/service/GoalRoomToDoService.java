package co.kirikiri.todo.service;

import co.kirikiri.common.aop.ExceptionConvert;
import co.kirikiri.common.exception.BadRequestException;
import co.kirikiri.common.exception.ForbiddenException;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomMember;
import co.kirikiri.goalroom.persistence.GoalRoomMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.todo.domain.GoalRoomToDo;
import co.kirikiri.todo.domain.GoalRoomToDoCheck;
import co.kirikiri.todo.domain.GoalRoomToDos;
import co.kirikiri.todo.persistence.GoalRoomToDoCheckRepository;
import co.kirikiri.todo.persistence.GoalRoomToDoRepository;
import co.kirikiri.todo.service.dto.request.GoalRoomTodoRequest;
import co.kirikiri.todo.service.dto.response.GoalRoomToDoCheckResponse;
import co.kirikiri.todo.service.dto.response.GoalRoomTodoResponse;
import co.kirikiri.todo.service.mapper.GoalRoomToDoMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@ExceptionConvert
public class GoalRoomToDoService {

    private final GoalRoomToDoRepository goalRoomToDoRepository;
    private final GoalRoomToDoCheckRepository goalRoomToDoCheckRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final MemberRepository memberRepository;

    public Long addGoalRoomTodo(final Long goalRoomId, final String identifier,
                                final GoalRoomTodoRequest goalRoomTodoRequest) {
        final Member member = findMemberByIdentifier(identifier);
        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        checkGoalRoomCompleted(goalRoom);
        checkGoalRoomLeader(member.getId(), goalRoom);
        final GoalRoomToDo goalRoomToDo = GoalRoomToDoMapper.convertToGoalRoomTodo(goalRoomTodoRequest, goalRoomId);
        return goalRoomToDoRepository.save(goalRoomToDo).getId();
    }

    private Member findMemberByIdentifier(final String memberIdentifier) {
        return memberRepository.findByIdentifier(new Identifier(memberIdentifier))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    private GoalRoom findGoalRoomById(final Long goalRoomId) {
        return goalRoomRepository.findById(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
    }

    private void checkGoalRoomCompleted(final GoalRoom goalRoom) {
        if (goalRoom.isCompleted()) {
            throw new BadRequestException("이미 종료된 골룸입니다.");
        }
    }

    private void checkGoalRoomLeader(final Long memberId, final GoalRoom goalRoom) {
        if (goalRoom.isNotLeader(memberId)) {
            throw new BadRequestException("골룸의 리더만 투두리스트를 추가할 수 있습니다.");
        }
    }

    public GoalRoomToDoCheckResponse checkGoalRoomTodo(final Long goalRoomId, final Long todoId,
                                                       final String identifier) {
        final Identifier memberIdentifier = new Identifier(identifier);
        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        final GoalRoomToDo goalRoomToDo = findGoalRoomTodoById(todoId);
        final GoalRoomMember goalRoomMember = findGoalRoomMember(memberIdentifier, goalRoom);

        final boolean isAlreadyChecked = goalRoomToDoCheckRepository.findByGoalRoomTodoAndGoalRoomMemberId(goalRoomToDo,
                goalRoomMember.getId()).isPresent();
        if (isAlreadyChecked) {
            goalRoomToDoCheckRepository.deleteByGoalRoomMemberIdAndToDoId(goalRoomMember.getId(), todoId);
            return new GoalRoomToDoCheckResponse(false);
        }
        final GoalRoomToDoCheck goalRoomToDoCheck = new GoalRoomToDoCheck(goalRoomMember.getId(), goalRoomToDo);
        goalRoomToDoCheckRepository.save(goalRoomToDoCheck);
        return new GoalRoomToDoCheckResponse(true);
    }

    private GoalRoomToDo findGoalRoomTodoById(final Long todoId) {
        return goalRoomToDoRepository.findById(todoId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 투두입니다. todoId = " + todoId));
    }

    private GoalRoomMember findGoalRoomMember(final Identifier memberIdentifier, final GoalRoom goalRoom) {
        final Member member = findMemberByIdentifier(memberIdentifier.getValue());
        return goalRoomMemberRepository.findByGoalRoomAndMemberId(goalRoom, member.getId())
                .orElseThrow(() -> new NotFoundException(
                        "골룸에 사용자가 존재하지 않습니다. goalRoomId = " + goalRoom.getId() + " memberIdentifier = "
                                + memberIdentifier.getValue()));
    }

    @Transactional(readOnly = true)
    public List<GoalRoomTodoResponse> findAllGoalRoomTodo(final Long goalRoomId, final String identifier) {
        final GoalRoomToDos goalRoomToDos = findGoalRoomTodosByGoalRoomId(goalRoomId);
        final GoalRoomMember goalRoomMember = findGoalRoomMember(goalRoomId, identifier);
        final List<GoalRoomToDoCheck> checkedTodos = findMemberCheckedGoalRoomToDos(goalRoomId, goalRoomMember.getId());
        return GoalRoomToDoMapper.convertGoalRoomTodoResponses(goalRoomToDos, checkedTodos);
    }

    private GoalRoomToDos findGoalRoomTodosByGoalRoomId(final Long goalRoomId) {
        findGoalRoomById(goalRoomId);
        return new GoalRoomToDos(goalRoomToDoRepository.findGoalRoomToDosByGoalRoomId(goalRoomId));
    }

    private GoalRoomMember findGoalRoomMember(final Long goalRoomId, final String identifier) {
        final Member member = findMemberByIdentifier(identifier);
        return goalRoomMemberRepository.findByGoalRoomIdAndMemberId(goalRoomId, member.getId())
                .orElseThrow(() -> new ForbiddenException(
                        "골룸에 참여하지 않은 사용자입니다. goalRoomId = " + goalRoomId + " memberIdentifier = " + identifier));
    }

    private List<GoalRoomToDoCheck> findMemberCheckedGoalRoomToDos(final Long goalRoomId, final Long goalRoomMemberId) {
        return goalRoomToDoCheckRepository.findByGoalRoomIdAndGoalRoomMemberId(goalRoomId, goalRoomMemberId);
    }
}
