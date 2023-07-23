package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseTimeEntity;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.exception.BadRequestException;
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
import java.time.LocalDate;
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

    @Embedded
    private LimitedMemberCount limitedMemberCount;

    @Enumerated(value = EnumType.STRING)
    private GoalRoomStatus status = GoalRoomStatus.RECRUITING;

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

    @Embedded
    private GoalRoomMembers goalRoomMembers;

    @Embedded
    private final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers();

    public GoalRoom(final String name, final LimitedMemberCount limitedMemberCount,
                    final RoadmapContent roadmapContent, final GoalRoomPendingMember goalRoomPendingMember) {
        this(null, name, limitedMemberCount, roadmapContent, goalRoomPendingMember);
    }

    public GoalRoom(final Long id, final String name, final LimitedMemberCount limitedMemberCount,
                    final RoadmapContent roadmapContent, final GoalRoomPendingMember goalRoomPendingMember) {
        this.id = id;
        this.name = name;
        this.limitedMemberCount = limitedMemberCount;
        this.roadmapContent = roadmapContent;
        goalRoomPendingMembers.add(goalRoomPendingMember);
        goalRoomPendingMember.updateGoalRoom(this);
    }

    public void updateStatus(final GoalRoomStatus status) {
        this.status = status;
    }

    public void addMember(final GoalRoomPendingMember member) {
        validateJoinGoalRoom(member);
        goalRoomPendingMembers.add(member);
        member.updateGoalRoom(this);
    }

    private void validateJoinGoalRoom(final GoalRoomPendingMember member) {
        validateMemberCount();
        validateStatus();
        validateParticipation(member);
    }

    private void validateMemberCount() {
        if (getCurrentMemberCount() >= limitedMemberCount.getValue()) {
            throw new BadRequestException("제한 인원이 꽉 찬 골룸에는 참여할 수 없습니다.");
        }
    }

    private void validateStatus() {
        if (!status.equals(GoalRoomStatus.RECRUITING)) {
            throw new BadRequestException("모집 중이지 않은 골룸에는 참여할 수 없습니다.");
        }
    }

    private void validateParticipation(final GoalRoomPendingMember member) {
        if (goalRoomPendingMembers.contains(member)) {
            throw new BadRequestException("이미 참여한 골룸에는 참여할 수 없습니다.");
        }
    }

    public void addRoadmapNodesAll(final GoalRoomRoadmapNodes goalRoomRoadmapNodes) {
        this.goalRoomRoadmapNodes.addAll(goalRoomRoadmapNodes);
    }

    public void addGoalRoomTodo(final GoalRoomToDo goalRoomToDo) {
        goalRoomToDos.add(goalRoomToDo);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCurrentMemberCount() {
        return goalRoomPendingMembers.getCurrentMemberCount();
    }

    public int getLimitedMemberCount() {
        return limitedMemberCount.getValue();
    }

    public LocalDate getGoalRoomStartDate() {
        return goalRoomRoadmapNodes.getGoalRoomStartDate();
    }

    public LocalDate getGoalRoomEndDate() {
        return goalRoomRoadmapNodes.getGoalRoomEndDate();
    }

    public GoalRoomPendingMember findLeader() {
        return goalRoomPendingMembers.findGoalRoomLeader();
    }

    public GoalRoomStatus getStatus() {
        return status;
    }

    public List<GoalRoomToDo> getTodos() {
        return goalRoomToDos;
    }
}
