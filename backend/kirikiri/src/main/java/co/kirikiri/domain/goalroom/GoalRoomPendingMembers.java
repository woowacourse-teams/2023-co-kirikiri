package co.kirikiri.domain.goalroom;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomPendingMembers {

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            mappedBy = "goalRoom",
            orphanRemoval = true)
    @Column(nullable = false)
    private final List<GoalRoomPendingMember> values = new ArrayList<>();

    public GoalRoomPendingMembers(final List<GoalRoomPendingMember> values) {
        this.values.addAll(new ArrayList<>(values));
    }

    public void add(final GoalRoomPendingMember goalRoomPendingMember) {
        values.add(goalRoomPendingMember);
    }

    public int size() {
        return values.size();
    }

    public boolean containGoalRoomPendingMember(final GoalRoomPendingMember goalRoomPendingMember) {
        return values.stream()
                .anyMatch(value -> value.equals(goalRoomPendingMember));
    }
}
