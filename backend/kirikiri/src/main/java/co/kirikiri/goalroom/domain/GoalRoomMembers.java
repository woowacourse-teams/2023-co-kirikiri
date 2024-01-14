package co.kirikiri.goalroom.domain;

import co.kirikiri.common.exception.UnexpectedDomainException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomMembers {

    private static final int MIN_SIZE_TO_FIND_NEXT_LEADER = 1;

    private final List<GoalRoomMember> values = new ArrayList<>();

    public GoalRoomMembers(final List<GoalRoomMember> values) {
        this.values.addAll(new ArrayList<>(values));
    }

    public boolean isNotLeader(final Long memberId) {
        final Long goalRoomLeaderId = findGoalRoomLeaderId();
        return !goalRoomLeaderId.equals(memberId);
    }

    public Long findGoalRoomLeaderId() {
        return values.stream()
                .filter(GoalRoomMember::isLeader)
                .findFirst()
                .map(GoalRoomMember::getMemberId)
                .orElseThrow(() -> new UnexpectedDomainException("골룸의 리더가 없습니다."));
    }

    public Optional<GoalRoomMember> findByMemberId(final Long memberId) {
        return values.stream()
                .filter(value -> value.isSameMember(memberId))
                .findFirst();
    }

    public void changeLeaderIfLeaderLeave(final GoalRoomMember leaveGoalRoomMember) {
        if (leaveGoalRoomMember.isLeader()) {
            findNextLeader().ifPresent(GoalRoomMember::becomeLeader);
        }
    }

    public Optional<GoalRoomMember> findNextLeader() {
        if (size() <= MIN_SIZE_TO_FIND_NEXT_LEADER) {
            return Optional.empty();
        }
        values.sort(Comparator.comparing(GoalRoomMember::getJoinedAt));
        return Optional.of(values.get(1));
    }

    public int size() {
        return values.size();
    }

    public List<GoalRoomMember> getValues() {
        return new ArrayList<>(values);
    }
}
