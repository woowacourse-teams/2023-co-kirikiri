package co.kirikiri.domain.goalroom;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomRoadmapNodes {

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @JoinColumn(name = "goal_room_id", nullable = false, updatable = false)
    private List<GoalRoomRoadmapNode> values;

    public GoalRoomRoadmapNodes(final List<GoalRoomRoadmapNode> values) {
        this.values = values;
    }

    public void addAll(final GoalRoomRoadmapNodes goalRoomRoadmapNodes) {
        values.addAll(new ArrayList<>(goalRoomRoadmapNodes.values));
    }

    public int size() {
        return values.size();
    }
}
