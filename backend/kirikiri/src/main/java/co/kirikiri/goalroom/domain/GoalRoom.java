package co.kirikiri.goalroom.domain;

import co.kirikiri.common.entity.BaseUpdatedTimeEntity;
import co.kirikiri.goalroom.domain.vo.GoalRoomName;
import co.kirikiri.goalroom.domain.vo.LimitedMemberCount;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoom extends BaseUpdatedTimeEntity {

    private static final int DATE_OFFSET = 1;

    @Embedded
    private GoalRoomName name;

    @Embedded
    private LimitedMemberCount limitedMemberCount;

    @Enumerated(value = EnumType.STRING)
    private GoalRoomStatus status = GoalRoomStatus.RECRUITING;

    private Long roadmapContentId;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Embedded
    private final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes();

    public GoalRoom(final GoalRoomName name, final LimitedMemberCount limitedMemberCount,
                    final Long roadmapContentId, final GoalRoomRoadmapNodes goalRoomRoadmapNodes) {
        this(null, name, limitedMemberCount, roadmapContentId, goalRoomRoadmapNodes);
    }

    public GoalRoom(final Long id, final GoalRoomName name, final LimitedMemberCount limitedMemberCount,
                    final Long roadmapContentId, final GoalRoomRoadmapNodes goalRoomRoadmapNodes) {
        this.id = id;
        this.name = name;
        this.limitedMemberCount = limitedMemberCount;
        this.roadmapContentId = roadmapContentId;
        addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
    }

    private void addAllGoalRoomRoadmapNodes(final GoalRoomRoadmapNodes goalRoomRoadmapNodes) {
        this.goalRoomRoadmapNodes.addAll(goalRoomRoadmapNodes);
        this.startDate = goalRoomRoadmapNodes.getGoalRoomStartDate();
        this.endDate = goalRoomRoadmapNodes.getGoalRoomEndDate();
    }

    public void start() {
        this.status = GoalRoomStatus.RUNNING;
    }

    public void complete() {
        this.status = GoalRoomStatus.COMPLETED;
    }

    public int calculateTotalPeriod() {
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + DATE_OFFSET;
    }

    public int getAllCheckCount() {
        return goalRoomRoadmapNodes.calculateAllCheckCount();
    }

    public boolean isRecruiting() {
        return status == GoalRoomStatus.RECRUITING;
    }

    public boolean isRunning() {
        return status == GoalRoomStatus.RUNNING;
    }

    public boolean isCompleted() {
        return this.status == GoalRoomStatus.COMPLETED;
    }

    public Optional<GoalRoomRoadmapNode> findNodeByDate(final LocalDate date) {
        return goalRoomRoadmapNodes.getNodeByDate(date);
    }

    public boolean cannotStart() {
        return startDate.isAfter(LocalDate.now());
    }

    public boolean isCompletedAfterMonths(final long numberOfMonth) {
        final LocalDate currentDate = LocalDate.now();
        return currentDate.isAfter(endDate.plusMonths(numberOfMonth));
    }

    public GoalRoomName getName() {
        return name;
    }

    public LimitedMemberCount getLimitedMemberCount() {
        return limitedMemberCount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public GoalRoomStatus getStatus() {
        return status;
    }

    public Long getRoadmapContentId() {
        return roadmapContentId;
    }

    public GoalRoomRoadmapNodes getGoalRoomRoadmapNodes() {
        return goalRoomRoadmapNodes;
    }
}
