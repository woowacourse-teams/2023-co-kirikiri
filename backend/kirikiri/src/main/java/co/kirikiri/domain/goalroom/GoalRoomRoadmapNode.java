package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseEntity;
import co.kirikiri.domain.roadmap.RoadmapNode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomRoadmapNode extends BaseEntity {

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer checkCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_node_id")
    private RoadmapNode roadmapNode;

    public GoalRoomRoadmapNode(final LocalDate startDate, final LocalDate endDate, final Integer checkCount,
                               final RoadmapNode roadmapNode) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.checkCount = checkCount;
        this.roadmapNode = roadmapNode;
    }
}
