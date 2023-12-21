package co.kirikiri.goalroom.domain;

import co.kirikiri.common.exception.UnexpectedDomainException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomPendingMembers {

    private static final int MIN_SIZE_TO_FIND_NEXT_LEADER = 1;

    @BatchSize(size = 20)
    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true, mappedBy = "goalRoom")
    private final List<GoalRoomPendingMember> values = new ArrayList<>();

    public GoalRoomPendingMembers(final List<GoalRoomPendingMember> values) {
        this.values.addAll(new ArrayList<>(values));
    }

    public void add(final GoalRoomPendingMember goalRoomPendingMember) {
        values.add(goalRoomPendingMember);
    }

    public boolean containGoalRoomPendingMember(final GoalRoomPendingMember goalRoomPendingMember) {
        return values.stream()
                .anyMatch(value -> value.isSameMember(goalRoomPendingMember.getMemberId()));
    }

    public boolean isMember(final Long memberId) {
        return values.stream()
                .anyMatch(value -> value.isSameMember(memberId));
    }

    public int size() {
        return values.size();
    }

    public Long findGoalRoomLeaderId() {
        return values.stream()
                .filter(GoalRoomPendingMember::isLeader)
                .findFirst()
                .map(GoalRoomPendingMember::getMemberId)
                .orElseThrow(() -> new UnexpectedDomainException("골룸의 리더가 없습니다."));
    }

    public boolean isNotLeader(final Long memberId) {
        final Long goalRoomLeaderId = findGoalRoomLeaderId();
        return !goalRoomLeaderId.equals(memberId);
    }

    public Optional<GoalRoomPendingMember> findByMemberId(final Long memberId) {
        return values.stream()
                .filter(value -> value.isSameMember(memberId))
                .findFirst();
    }

    public Optional<GoalRoomPendingMember> findNextLeader() {
        if (size() <= MIN_SIZE_TO_FIND_NEXT_LEADER) {
            return Optional.empty();
        }
        values.sort(Comparator.comparing(GoalRoomPendingMember::getJoinedAt));
        return Optional.of(values.get(1));
    }

    public void remove(final GoalRoomPendingMember goalRoomPendingMember) {
        values.remove(goalRoomPendingMember);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public void deleteAll() {
        this.values.clear();
    }

    public List<GoalRoomPendingMember> getValues() {
        return new ArrayList<>(values);
    }
}
