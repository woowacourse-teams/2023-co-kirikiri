package co.kirikiri.goalroom.service;

import co.kirikiri.common.aop.ExceptionConvert;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomPendingMember;
import co.kirikiri.goalroom.domain.GoalRoomRole;
import co.kirikiri.goalroom.persistence.GoalRoomPendingMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomRepository;
import co.kirikiri.goalroom.service.event.GoalRoomLeaderUpdateEvent;
import co.kirikiri.persistence.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@ExceptionConvert
public class GoalRoomLeaderUpdateEventListener {

    private final GoalRoomRepository goalRoomRepository;
    private final MemberRepository memberRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @Transactional
    public void handleGoalRoomLeaderUpdate(final GoalRoomLeaderUpdateEvent goalRoomLeaderUpdateEvent) {
        final Member leader = findMemberByIdentifier(goalRoomLeaderUpdateEvent.leaderIdentifier());
        final GoalRoom goalRoom = findGoalRoomById(goalRoomLeaderUpdateEvent);

        final GoalRoomPendingMember goalRoomLeader = new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom,
                leader.getId());
        goalRoomPendingMemberRepository.save(goalRoomLeader);
    }

    private Member findMemberByIdentifier(final String memberIdentifier) {
        return memberRepository.findByIdentifier(new Identifier(memberIdentifier))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    private GoalRoom findGoalRoomById(final GoalRoomLeaderUpdateEvent goalRoomLeaderUpdateEvent) {
        return goalRoomRepository.findById(goalRoomLeaderUpdateEvent.goalRoomId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다."));
    }
}
