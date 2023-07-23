package co.kirikiri.domain.goalroom;

import co.kirikiri.exception.NotFoundException;
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

    public LocalDate getGoalRoomStartDate() {
        return values.stream()
                .min(Comparator.comparing(GoalRoomRoadmapNode::getStartDate))
                .orElseThrow(() -> new NotFoundException("골룸에 노드가 존재하지 않습니다."))
                .getStartDate();
    }

    public LocalDate getGoalRoomEndDate() {
        return values.stream()
                .max(Comparator.comparing(GoalRoomRoadmapNode::getEndDate))
                .orElseThrow(() -> new NotFoundException("골룸에 노드가 존재하지 않습니다."))
                .getEndDate();
    }
}
