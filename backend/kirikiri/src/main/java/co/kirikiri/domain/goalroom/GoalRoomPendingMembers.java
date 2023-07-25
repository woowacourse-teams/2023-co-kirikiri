package co.kirikiri.domain.goalroom;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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

    public boolean containGaolRoomPendingMember(final GoalRoomPendingMember goalRoomPendingMember) {
        return values.stream()
                .anyMatch(value -> value.equals(goalRoomPendingMember));
    }
}
