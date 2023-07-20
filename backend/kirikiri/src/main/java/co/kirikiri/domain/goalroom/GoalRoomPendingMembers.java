package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.member.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomPendingMembers {

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            mappedBy = "goalRoom",
            orphanRemoval = true)
    @Column(nullable = false)
    private List<GoalRoomPendingMember> values;

    public GoalRoomPendingMembers(final List<GoalRoomPendingMember> values) {
        this.values = values;
    }

    public void add(final GoalRoomPendingMember goalRoomPendingMember) {
        values.add(new GoalRoomPendingMember(goalRoomPendingMember));
    }

    public int size() {
        return values.size();
    }

    public boolean containMember(final Member member) {
        return values.stream()
                .anyMatch(value -> value.isEqualMember(member));
    }
}
