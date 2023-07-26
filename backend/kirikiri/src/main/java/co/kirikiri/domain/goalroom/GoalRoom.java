package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseTimeEntity;
import co.kirikiri.domain.roadmap.RoadmapContent;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    private Integer limitedMemberCount = 0;

    private Integer currentMemberCount = 0;

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
    private GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes();

    @Embedded
    private GoalRoomMembers goalRoomMembers = new GoalRoomMembers();

    public GoalRoom(final String name, final Integer limitedMemberCount, final Integer currentMemberCount,
                    final GoalRoomStatus status, final RoadmapContent roadmapContent) {
        this.name = name;
        this.limitedMemberCount = limitedMemberCount;
        this.currentMemberCount = currentMemberCount;
        this.status = status;
        this.roadmapContent = roadmapContent;
    }

    public void addGoalRoomRoadmapNodes(final GoalRoomRoadmapNodes goalRoomRoadmapNodes) {
        this.goalRoomRoadmapNodes.addAll(goalRoomRoadmapNodes);
    }

    public void addMember(final GoalRoomMember goalRoomMember) {
        goalRoomMembers.add(goalRoomMember);
    }

    public void complete() {
        status = GoalRoomStatus.COMPLETED;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RoadmapContent getRoadmapContent() {
        return roadmapContent;
    }

    public GoalRoomStatus getStatus() {
        return status;
    }
}
