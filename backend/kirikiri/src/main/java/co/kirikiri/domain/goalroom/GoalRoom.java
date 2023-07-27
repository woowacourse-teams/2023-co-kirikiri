package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseCreatedTimeEntity;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.exception.BadRequestException;
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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoom extends BaseCreatedTimeEntity {

    private static final int DATE_OFFSET = 1;

    @Embedded
    private GoalRoomName name;

    @Embedded
    private LimitedMemberCount limitedMemberCount;

    @Enumerated(value = EnumType.STRING)
    private GoalRoomStatus status = GoalRoomStatus.RECRUITING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_content_id", nullable = false)
    private RoadmapContent roadmapContent;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

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
        this(null, name, limitedMemberCount, roadmapContent);
    }

    public GoalRoom(final Long id, final GoalRoomName name, final LimitedMemberCount limitedMemberCount, final RoadmapContent roadmapContent) {
        this.id = id;
        this.name = name;
        this.limitedMemberCount = limitedMemberCount;
        this.roadmapContent = roadmapContent;
    }

    public void participate(final GoalRoomPendingMember goalRoomPendingMember) {
        if (limitedMemberCount.isLessAndEqualsThan(goalRoomPendingMembers.size())) {
            throw new BadRequestException("정원 초과입니다.");
        }
        checkParticipated(goalRoomPendingMember);
        goalRoomPendingMembers.add(goalRoomPendingMember);
        goalRoomPendingMember.updateGoalRoom(this);
    }

    private void checkParticipated(final GoalRoomPendingMember goalRoomPendingMember) {
        if (goalRoomPendingMembers.containGoalRoomPendingMember(goalRoomPendingMember)) {
            throw new BadRequestException("이미 참여 중인 상태입니다.");
        }
    }

    public int calculateTotalPeriod() {
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + DATE_OFFSET;
    }
    
    public boolean isRecruiting() {
        return status == GoalRoomStatus.RECRUITING;
    }

    public void addAllGoalRoomRoadmapNodes(final GoalRoomRoadmapNodes goalRoomRoadmapNodes) {
        checkTotalSize(goalRoomRoadmapNodes.size() + this.goalRoomRoadmapNodes.size());
        this.goalRoomRoadmapNodes.addAll(goalRoomRoadmapNodes);
        this.startDate = goalRoomRoadmapNodes.getGoalRoomStartDate();
        this.endDate = goalRoomRoadmapNodes.getGoalRoomEndDate();
    }

    private void checkTotalSize(final int totalSize) {
        if (totalSize > roadmapContent.nodesSize()) {
            throw new BadRequestException("로드맵의 노드 수보다 골룸의 노드 수가 큽니다.");
        }
    }

    public void addGoalRoomTodo(final GoalRoomToDo goalRoomToDo) {
        goalRoomToDos.add(goalRoomToDo);
    }

    @Override
    public Long getId() {
        return id;
    }

    public GoalRoomName getName() {
        return name;
    }

    public LimitedMemberCount getLimitedMemberCount() {
        return limitedMemberCount;
    }

    public Integer getCurrentPendingMemberCount() {
        return goalRoomPendingMembers.size();
    }

    public GoalRoomRoadmapNodes getGoalRoomRoadmapNodes() {
        return goalRoomRoadmapNodes;
    }


}
