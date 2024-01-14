package co.kirikiri.checkfeed.service;

import co.kirikiri.checkfeed.service.event.AccomplishmentRateUpdateEvent;
import co.kirikiri.common.aop.ExceptionConvert;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomMember;
import co.kirikiri.goalroom.persistence.GoalRoomMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@ExceptionConvert
public class AccomplishmentRateUpdateEventListener {

    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @Transactional
    public void handleUpdateAccomplishmentRate(final AccomplishmentRateUpdateEvent accomplishmentRateUpdateEvent) {
        final GoalRoom goalRoom = findGoalRoomById(accomplishmentRateUpdateEvent.goalRoomId());
        final GoalRoomMember goalRoomMember = findGoalRoomMemberById(accomplishmentRateUpdateEvent.goalRoomMemberId());

        updateAccomplishmentRate(goalRoom, goalRoomMember, accomplishmentRateUpdateEvent.pastCheckCount());
    }

    private GoalRoom findGoalRoomById(final Long goalRoomId) {
        return goalRoomRepository.findById(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다."));
    }

    private GoalRoomMember findGoalRoomMemberById(final Long goalRoomMemberId) {
        return goalRoomMemberRepository.findById(goalRoomMemberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸 멤버입니다."));
    }

    private void updateAccomplishmentRate(final GoalRoom goalRoom, final GoalRoomMember goalRoomMember,
                                          final int pastCheckCount) {
        final int wholeCheckCount = goalRoom.getAllCheckCount();
        final int memberCheckCount = pastCheckCount + 1;
        final Double accomplishmentRate = 100 * memberCheckCount / (double) wholeCheckCount;

        goalRoomMember.updateAccomplishmentRate(accomplishmentRate);
    }
}
