package co.kirikiri.goalroom.domain;

import co.kirikiri.common.exception.UnexpectedDomainException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomPendingMembers {

    private static final int MIN_SIZE_TO_FIND_NEXT_LEADER = 1;

    private final List<GoalRoomPendingMember> values = new ArrayList<>();

    public GoalRoomPendingMembers(final List<GoalRoomPendingMember> values) {
        this.values.addAll(new ArrayList<>(values));
    }

    public boolean containGoalRoomPendingMember(final Long memberId) {
        return values.stream()
                .anyMatch(value -> value.isSameMember(memberId));
    }

    public boolean isNotLeader(final Long memberId) {
        final Long goalRoomLeaderId = findGoalRoomLeaderId();
        return !goalRoomLeaderId.equals(memberId);
    }

    public Long findGoalRoomLeaderId() {
        return values.stream()
                .filter(GoalRoomPendingMember::isLeader)
                .findFirst()
                .map(GoalRoomPendingMember::getMemberId)
                .orElseThrow(() -> new UnexpectedDomainException("골룸의 리더가 없습니다."));
    }

    public Optional<GoalRoomPendingMember> findByMemberId(final Long memberId) {
        return values.stream()
                .filter(value -> value.isSameMember(memberId))
                .findFirst();
    }

    public void changeLeaderIfLeaderLeave(final GoalRoomPendingMember leaveGoalRoomPendingMember) {
        if (leaveGoalRoomPendingMember.isLeader()) {
            findNextLeader().ifPresent(GoalRoomPendingMember::becomeLeader);
        }
    }

    public Optional<GoalRoomPendingMember> findNextLeader() {
        if (size() <= MIN_SIZE_TO_FIND_NEXT_LEADER) {
            return Optional.empty();
        }
        values.sort(Comparator.comparing(GoalRoomPendingMember::getJoinedAt));
        return Optional.of(values.get(1));
    }

    public int size() {
        return values.size();
    }

    public List<GoalRoomPendingMember> getValues() {
        return new ArrayList<>(values);
    }
}
