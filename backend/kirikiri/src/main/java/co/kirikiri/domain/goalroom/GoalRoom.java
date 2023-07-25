package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseCreatedTimeEntity;
import co.kirikiri.domain.roadmap.RoadmapContent;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoom extends BaseCreatedTimeEntity {

    @Column(nullable = false, length = 50)
    private String name;

    private Integer limitedMemberCount = 0;

    @Enumerated(value = EnumType.STRING)
    private GoalRoomStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_content_id", nullable = false)
    private RoadmapContent roadmapContent;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @JoinColumn(name = "goal_room_id", nullable = false, updatable = false)
    private final List<GoalRoomToDo> goalRoomToDos = new ArrayList<>();

    @Embedded
    private final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes();

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true, mappedBy = "goalRoom")
    private final List<GoalRoomMember> goalRoomMembers = new ArrayList<>();

    public GoalRoom(final String name, final Integer limitedMemberCount, final GoalRoomStatus status,
                    final RoadmapContent roadmapContent) {
        this.name = name;
        this.limitedMemberCount = limitedMemberCount;
        this.status = status;
        this.roadmapContent = roadmapContent;
    }

    public void addGoalRoomRoadmapNodes(final GoalRoomRoadmapNodes goalRoomRoadmapNodes) {
        this.goalRoomRoadmapNodes.addAll(goalRoomRoadmapNodes);
    }

    public int calculateTotalPeriod() {
        return goalRoomRoadmapNodes.addTotalPeriod();
    }

    public boolean isRecruiting() {
        return status == GoalRoomStatus.RECRUITING;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public GoalRoomRoadmapNodes getGoalRoomRoadmapNodes() {
        return goalRoomRoadmapNodes;
    }
}
