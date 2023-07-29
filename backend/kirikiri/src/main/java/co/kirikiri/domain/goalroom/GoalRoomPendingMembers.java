package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.member.Member;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.exception.ServerException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomPendingMembers {

    private static final int MIN_SIZE_TO_FIND_NEXT_LEADER = 1;

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
                .anyMatch(value -> value.equals(goalRoomPendingMember));
    }

    public Member findGoalRoomLeader() {
        return values.stream()
                .filter(GoalRoomPendingMember::isLeader)
                .findFirst()
                .map(GoalRoomPendingMember::getMember)
                .orElseThrow(() -> new NotFoundException("골룸의 리더가 없습니다."));
    }

    public GoalRoomPendingMember findByMember(final Member member) {
        return values.stream()
                .filter(value -> value.isSameMemberWith(member))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("골룸에 참여한 사용자가 아닙니다. memberId = " + member.getId()));
    }

    public GoalRoomPendingMember findNextLeader() {
        if (size() <= MIN_SIZE_TO_FIND_NEXT_LEADER) {
            throw new ServerException(
                    String.format("골룸 참여자가 %d명 이하이므로 다음 리더를 찾을 수 없습니다.", MIN_SIZE_TO_FIND_NEXT_LEADER));
        }
        values.sort(Comparator.comparing(GoalRoomPendingMember::getJoinedAt));
        return values.get(1);
    }

    public void remove(final GoalRoomPendingMember goalRoomPendingMember) {
        values.remove(goalRoomPendingMember);
    }

    public int size() {
        return values.size();
    }

    public List<GoalRoomPendingMember> getValues() {
        return new ArrayList<>(values);
    }
}
