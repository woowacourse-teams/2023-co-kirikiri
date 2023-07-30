package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.member.Member;
import co.kirikiri.exception.NotFoundException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.List;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomMembers {

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true, mappedBy = "goalRoom")
    private List<GoalRoomMember> values;

    public GoalRoomMembers(final List<GoalRoomMember> values) {
        this.values = values;
    }

    public void add(final GoalRoomMember goalRoomMember) {
        this.values.add(goalRoomMember);
    }

    public boolean isNotLeader(final Member member) {
        final Member goalRoomLeader = findGoalRoomLeader();
        return !goalRoomLeader.equals(member);
    }

    private Member findGoalRoomLeader() {
        return values.stream()
                .filter(GoalRoomMember::isLeader)
                .findFirst()
                .map(GoalRoomMember::getMember)
                .orElseThrow(() -> new NotFoundException("골룸의 리더가 없습니다."));
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public List<GoalRoomMember> getValues() {
        return values;
    }
}
