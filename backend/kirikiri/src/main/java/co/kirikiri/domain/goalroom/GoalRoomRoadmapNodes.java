package co.kirikiri.domain.goalroom;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomRoadmapNodes {

    private static final int FIRST_GOAL_ROOM_NODE_INDEX = 0;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @JoinColumn(name = "goal_room_id", nullable = false, updatable = false)
    private List<GoalRoomRoadmapNode> values = new ArrayList<>();

    public GoalRoomRoadmapNodes(final List<GoalRoomRoadmapNode> values) {
        this.values = values;
    }

    public void addAll(final GoalRoomRoadmapNodes goalRoomRoadmapNodes) {
        this.values.addAll(goalRoomRoadmapNodes.values);
    }

    public LocalDate getGoalRoomStartDate() {
        sortByStartDate();
        return values.get(FIRST_GOAL_ROOM_NODE_INDEX).getStartDate();
    }

    public LocalDate getGoalRoomEndDate() {
        sortByStartDate();
        return values.get(values.size() - 1).getEndDate();
    }

    private void sortByStartDate() {
        values.sort(Comparator.comparing(GoalRoomRoadmapNode::getStartDate));
    }
}
