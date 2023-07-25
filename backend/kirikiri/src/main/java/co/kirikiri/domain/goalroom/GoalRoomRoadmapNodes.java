package co.kirikiri.domain.goalroom;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomRoadmapNodes {

    private static final int DATE_OFFSET = 1;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @JoinColumn(name = "goal_room_id", nullable = false, updatable = false)
    private final List<GoalRoomRoadmapNode> values = new ArrayList<>();

    public GoalRoomRoadmapNodes(final List<GoalRoomRoadmapNode> goalRoomRoadmapNodes) {
        this.values.addAll(new ArrayList<>(goalRoomRoadmapNodes));
    }

    public void addAll(final GoalRoomRoadmapNodes goalRoomRoadmapNodes) {
        this.values.addAll(new ArrayList<>(goalRoomRoadmapNodes.values));
    }

    public int addTotalPeriod() {
        return values.stream()
                .mapToInt(this::calculatePeriod)
                .sum();
    }

    private int calculatePeriod(final GoalRoomRoadmapNode node) {
        return (int) ChronoUnit.DAYS.between(node.getStartDate(), node.getEndDate()) + DATE_OFFSET;
    }

    public List<GoalRoomRoadmapNode> getValues() {
        return new ArrayList<>(values);
    }
}
