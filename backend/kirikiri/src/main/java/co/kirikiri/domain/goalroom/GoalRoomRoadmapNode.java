package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.roadmap.RoadmapNode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomRoadmapNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    private final Integer checkCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_node_id")
    private RoadmapNode roadmapNode;

    public GoalRoomRoadmapNode(final LocalDate startDate, final LocalDate endDate, final RoadmapNode roadmapNode) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.roadmapNode = roadmapNode;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
