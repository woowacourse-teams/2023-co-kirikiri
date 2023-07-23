package co.kirikiri.domain.goalroom;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomRoadmapNodes {

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @JoinColumn(name = "goal_room_id", nullable = false, updatable = false)
    private List<GoalRoomRoadmapNode> values = new ArrayList<>();

    public GoalRoomRoadmapNodes(final List<GoalRoomRoadmapNode> nodes) {
        this.values = nodes;
    }

    public void addAll(final GoalRoomRoadmapNodes goalRoomRoadmapNodes) {
        this.values.addAll(goalRoomRoadmapNodes.getValues());
    }

    public List<GoalRoomRoadmapNode> getValues() {
        return values;
    }
}
