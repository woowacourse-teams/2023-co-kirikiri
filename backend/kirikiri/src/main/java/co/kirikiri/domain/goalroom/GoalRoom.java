package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseTimeEntity;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.CascadeType;
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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private GoalRoomName name;

    @Embedded
    private LimitedMemberCount limitedMemberCount;

    @Enumerated(value = EnumType.STRING)
    private final GoalRoomStatus status = GoalRoomStatus.RECRUITING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_content_id", nullable = false)
    private RoadmapContent roadmapContent;

    @Embedded
    private GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(new ArrayList<>());

    @Embedded
    private GoalRoomToDos goalRoomToDos = new GoalRoomToDos(new ArrayList<>());

    @Embedded
    private GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(new ArrayList<>());

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true, mappedBy = "goalRoom")
    private final List<GoalRoomMember> goalRoomMembers = new ArrayList<>();

    public GoalRoom(final GoalRoomName name, final LimitedMemberCount limitedMemberCount, final RoadmapContent roadmapContent) {
        this.name = name;
        this.limitedMemberCount = limitedMemberCount;
        this.roadmapContent = roadmapContent;
    }

    public GoalRoom(final Long id, final GoalRoomName name, final LimitedMemberCount limitedMemberCount, final RoadmapContent roadmapContent) {
        this.id = id;
        this.name = name;
        this.limitedMemberCount = limitedMemberCount;
        this.roadmapContent = roadmapContent;
    }

    public void participate(final GoalRoomPendingMember goalRoomPendingMember) {
        if (limitedMemberCount.isMoreThan(goalRoomPendingMembers.size())) {
            checkParticipated(goalRoomPendingMember);
            goalRoomPendingMembers.add(goalRoomPendingMember);
            goalRoomPendingMember.updateGoalRoom(this);
            return;
        }
        throw new BadRequestException("정원 초과입니다.");
    }

    private void checkParticipated(final GoalRoomPendingMember goalRoomPendingMember) {
        if (goalRoomPendingMembers.containMember(goalRoomPendingMember.getMember())) {
            throw new BadRequestException("이미 참여 중인 상태입니다.");
        }
    }

    public void addAllGoalRoomRoadmapNodes(final GoalRoomRoadmapNodes goalRoomRoadmapNodes) {
        checkTotalSize(goalRoomRoadmapNodes.size() + this.goalRoomRoadmapNodes.size());
        goalRoomRoadmapNodes.addAll(goalRoomRoadmapNodes);
    }

    private void checkTotalSize(final int totalSize) {
        if (totalSize > roadmapContent.nodesSize()) {
            throw new BadRequestException("로드맵의 노드 수보다 골룸의 노드 수가 큽니다.");
        }
    }

    public void addGoalRoomTodo(final GoalRoomToDo goalRoomToDo) {
        goalRoomToDos.add(goalRoomToDo);
    }

    public Long getId() {
        return id;
    }
}
