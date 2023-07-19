package co.kirikiri.domain.goalroom;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomMembers {

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true, mappedBy = "goalRoom")
    private final List<GoalRoomMember> values = new ArrayList<>();

    public List<GoalRoomMember> getValues() {
        return values;
    }
}
