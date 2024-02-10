package co.kirikiri.checkfeed.service;

import co.kirikiri.checkfeed.domain.CheckFeed;
import co.kirikiri.checkfeed.persistence.CheckFeedRepository;
import co.kirikiri.checkfeed.service.event.CheckFeedCreateEvent;
import co.kirikiri.common.aop.ExceptionConvert;
import co.kirikiri.common.exception.BadRequestException;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomMember;
import co.kirikiri.goalroom.domain.GoalRoomRoadmapNode;
import co.kirikiri.goalroom.persistence.GoalRoomMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@ExceptionConvert
public class CheckFeedCreateEventListener {

    private final CheckFeedRepository checkFeedRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @Transactional
    public void handleUpdateAccomplishmentRate(final CheckFeedCreateEvent checkFeedCreateEvent) {
        final CheckFeed checkFeed = findCheckFeedById(checkFeedCreateEvent.checkFeedId());
        final GoalRoom goalRoom = findGoalRoomById(checkFeedCreateEvent.goalRoomId());
        final GoalRoomMember goalRoomMember = findGoalRoomMemberById(checkFeed.getGoalRoomMemberId());
        final GoalRoomRoadmapNode currentNode = getNodeByDate(goalRoom);
        final int currentCheckCount = getCurrentCheckCount(goalRoomMember, currentNode);

        updateAccomplishmentRate(goalRoom, goalRoomMember, currentCheckCount);
    }

    private CheckFeed findCheckFeedById(final Long checkFeedId) {
        return checkFeedRepository.findById(checkFeedId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 인증피드입니다."));
    }

    private GoalRoom findGoalRoomById(final Long goalRoomId) {
        return goalRoomRepository.findById(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다."));
    }

    private GoalRoomMember findGoalRoomMemberById(final Long goalRoomMemberId) {
        return goalRoomMemberRepository.findById(goalRoomMemberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸 멤버입니다."));
    }

    private GoalRoomRoadmapNode getNodeByDate(final GoalRoom goalRoom) {
        return goalRoom.findNodeByDate(LocalDate.now())
                .orElseThrow(() -> new BadRequestException("인증 피드는 노드 기간 내에만 작성할 수 있습니다."));
    }

    private int getCurrentCheckCount(final GoalRoomMember goalRoomMember, final GoalRoomRoadmapNode currentNode) {
        return checkFeedRepository.countByGoalRoomMemberIdAndGoalRoomRoadmapNodeId(
                goalRoomMember.getId(), currentNode.getId());
    }

    private void updateAccomplishmentRate(final GoalRoom goalRoom, final GoalRoomMember goalRoomMember,
                                          final int currentCheckCount) {
        final int wholeCheckCount = goalRoom.getAllCheckCount();
        final Double accomplishmentRate = 100 * currentCheckCount / (double) wholeCheckCount;

        goalRoomMember.updateAccomplishmentRate(accomplishmentRate);
    }
}
