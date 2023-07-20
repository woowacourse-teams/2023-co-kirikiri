package co.kirikiri.domain.goalroom;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomPendingMembers {

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true, mappedBy = "goalRoom")
    private final List<GoalRoomPendingMember> values = new ArrayList<>();

    public void add(final GoalRoomPendingMember member) {
        values.add(member);
    }

    public boolean contains(final GoalRoomPendingMember member) {
        return values.stream()
                .anyMatch(goalRoomPendingMember -> Objects.equals(member.getMember().getId(),
                        goalRoomPendingMember.getMember().getId()));
    }

    public int getCurrentMemberCount() {
        return values.size();
    }

    public List<GoalRoomPendingMember> getValues() {
        return values;
    }
}
