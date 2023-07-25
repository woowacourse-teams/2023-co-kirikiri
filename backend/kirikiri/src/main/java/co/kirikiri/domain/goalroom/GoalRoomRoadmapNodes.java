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
import org.hibernate.annotations.BatchSize;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomRoadmapNodes {

    @BatchSize(size = 10)
    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @JoinColumn(name = "goal_room_id", nullable = false, updatable = false)
    private final List<GoalRoomRoadmapNode> values = new ArrayList<>();

    public GoalRoomRoadmapNodes(final List<GoalRoomRoadmapNode> goalRoomRoadmapNodes) {
        this.values.addAll(goalRoomRoadmapNodes);
    }

    public void addAll(final GoalRoomRoadmapNodes goalRoomRoadmapNodes) {
        this.values.addAll(goalRoomRoadmapNodes.values);
    }

    public List<GoalRoomRoadmapNode> getValues() {
        return values;
    }
}
