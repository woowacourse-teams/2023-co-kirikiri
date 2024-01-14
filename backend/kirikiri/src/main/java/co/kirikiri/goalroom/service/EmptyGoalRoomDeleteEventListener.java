package co.kirikiri.goalroom.service;

import co.kirikiri.common.aop.ExceptionConvert;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomMember;
import co.kirikiri.goalroom.domain.GoalRoomPendingMember;
import co.kirikiri.goalroom.persistence.GoalRoomMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomPendingMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomRepository;
import co.kirikiri.goalroom.service.event.EmptyGoalRoomDeleteEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@ExceptionConvert
public class EmptyGoalRoomDeleteEventListener {

    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;

    @Async
    @TransactionalEventListener
    @Transactional
    public void handleDeleteEmptyGoalRoom(final EmptyGoalRoomDeleteEvent emptyGoalRoomDeleteEvent) {
        final GoalRoom goalRoom = findGoalRoomById(emptyGoalRoomDeleteEvent.goalRoomId());
        final List<GoalRoomMember> goalRoomMembers = goalRoomMemberRepository.findByGoalRoom(goalRoom);
        final List<GoalRoomPendingMember> goalRoomPendingMembers = goalRoomPendingMemberRepository.findByGoalRoom(
                goalRoom);

        if (goalRoomMembers.isEmpty() && goalRoomPendingMembers.isEmpty()) {
            goalRoomRepository.delete(goalRoom);
        }
    }

    private GoalRoom findGoalRoomById(final Long goalRoomId) {
        return goalRoomRepository.findById(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다."));
    }
}
