package co.kirikiri.goalroom.domain;

import co.kirikiri.common.entity.BaseUpdatedTimeEntity;
import co.kirikiri.goalroom.domain.exception.GoalRoomException;
import co.kirikiri.goalroom.domain.vo.GoalRoomName;
import co.kirikiri.goalroom.domain.vo.LimitedMemberCount;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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
    private final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers();

    @Embedded
    private final GoalRoomMembers goalRoomMembers = new GoalRoomMembers();

    @Embedded
    private final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes();

    public GoalRoom(final GoalRoomName name, final LimitedMemberCount limitedMemberCount,
                    final Long roadmapContentId, final Long memberId) {
        this(null, name, limitedMemberCount, roadmapContentId, memberId);
    }

    public GoalRoom(final Long id, final GoalRoomName name, final LimitedMemberCount limitedMemberCount,
                    final Long roadmapContentId, final Long memberId) {
        this.id = id;
        this.name = name;
        this.limitedMemberCount = limitedMemberCount;
        this.roadmapContentId = roadmapContentId;
        updateLeader(memberId);
    }

    private void updateLeader(final Long memberId) {
        final GoalRoomPendingMember leader = new GoalRoomPendingMember(GoalRoomRole.LEADER, memberId);
        leader.initGoalRoom(this);
        goalRoomPendingMembers.add(leader);
    }

    public void addAllGoalRoomRoadmapNodes(final GoalRoomRoadmapNodes goalRoomRoadmapNodes) {
        this.goalRoomRoadmapNodes.addAll(goalRoomRoadmapNodes);
        this.startDate = goalRoomRoadmapNodes.getGoalRoomStartDate();
        this.endDate = goalRoomRoadmapNodes.getGoalRoomEndDate();
    }

    public void join(final Long memberId) {
        final GoalRoomPendingMember newMember = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, memberId);
        newMember.initGoalRoom(this);
        validateJoinGoalRoom(newMember);
        goalRoomPendingMembers.add(newMember);
    }

    private void validateJoinGoalRoom(final GoalRoomPendingMember member) {
        validateMemberCount();
        validateStatus();
        validateAlreadyParticipated(member);
    }

    private void validateMemberCount() {
        if (getCurrentMemberCount() >= limitedMemberCount.getValue()) {
            throw new GoalRoomException("제한 인원이 꽉 찬 골룸에는 참여할 수 없습니다.");
        }
    }

    private void validateStatus() {
        if (status != GoalRoomStatus.RECRUITING) {
            throw new GoalRoomException("모집 중이지 않은 골룸에는 참여할 수 없습니다.");
        }
    }

    private void validateAlreadyParticipated(final GoalRoomPendingMember member) {
        if (goalRoomPendingMembers.containGoalRoomPendingMember(member)) {
            throw new GoalRoomException("이미 참여한 골룸에는 참여할 수 없습니다.");
        }
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

    public int goalRoomRoadmapNodeSize() {
        return this.goalRoomRoadmapNodes.size();
    }

    public Long findGoalRoomLeaderId() {
        if (status == GoalRoomStatus.RECRUITING) {
            return goalRoomPendingMembers.findGoalRoomLeaderId();
        }
        return goalRoomMembers.findGoalRoomLeaderId();
    }

    public boolean isNotLeader(final Long memberId) {
        if (status == GoalRoomStatus.RECRUITING) {
            return goalRoomPendingMembers.isNotLeader(memberId);
        }
        return goalRoomMembers.isNotLeader(memberId);
    }

    public Optional<GoalRoomRoadmapNode> findNodeByDate(final LocalDate date) {
        return goalRoomRoadmapNodes.getNodeByDate(date);
    }

    public Integer getCurrentMemberCount() {
        if (status == GoalRoomStatus.RECRUITING) {
            return goalRoomPendingMembers.size();
        }
        return goalRoomMembers.size();
    }

    public void addAllGoalRoomMembers(final List<GoalRoomMember> members) {
        this.goalRoomMembers.addAll(new ArrayList<>(members));
    }

    public boolean isGoalRoomMember(final Long memberId) {
        if (status == GoalRoomStatus.RECRUITING) {
            return goalRoomPendingMembers.isMember(memberId);
        }
        return goalRoomMembers.isMember(memberId);
    }

    public void leave(final Long memberId) {
        if (status == GoalRoomStatus.RECRUITING) {
            final GoalRoomPendingMember goalRoomPendingMember = findGoalRoomPendingMemberByMember(memberId);
            changeRoleIfLeaderLeave(goalRoomPendingMembers, goalRoomPendingMember);
            goalRoomPendingMembers.remove(goalRoomPendingMember);
            return;
        }
        final GoalRoomMember goalRoomMember = findGoalRoomMemberByMember(memberId);
        changeRoleIfLeaderLeave(goalRoomMembers, goalRoomMember);
        goalRoomMembers.remove(goalRoomMember);
    }

    public boolean cannotStart() {
        return startDate.isAfter(LocalDate.now());
    }

    private GoalRoomPendingMember findGoalRoomPendingMemberByMember(final Long memberId) {
        return goalRoomPendingMembers.findByMemberId(memberId)
                .orElseThrow(() -> new GoalRoomException("골룸에 참여한 사용자가 아닙니다. memberId = " + memberId));
    }

    private void changeRoleIfLeaderLeave(final GoalRoomPendingMembers goalRoomPendingMembers,
                                         final GoalRoomPendingMember goalRoomPendingMember) {
        if (goalRoomPendingMember.isLeader()) {
            goalRoomPendingMembers.findNextLeader()
                    .ifPresent(GoalRoomPendingMember::becomeLeader);
        }
    }

    private GoalRoomMember findGoalRoomMemberByMember(final Long memberId) {
        return goalRoomMembers.findByMemberId(memberId)
                .orElseThrow(() -> new GoalRoomException("골룸에 참여한 사용자가 아닙니다. memberId = " + memberId));
    }

    private void changeRoleIfLeaderLeave(final GoalRoomMembers goalRoomMembers,
                                         final GoalRoomMember goalRoomMember) {
        if (goalRoomMember.isLeader()) {
            goalRoomMembers.findNextLeader()
                    .ifPresent(GoalRoomMember::becomeLeader);
        }
    }

    public boolean isEmptyGoalRoom() {
        return goalRoomPendingMembers.isEmpty() && goalRoomMembers.isEmpty();
    }

    public void deleteAllPendingMembers() {
        goalRoomPendingMembers.deleteAll();
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

    public GoalRoomPendingMembers getGoalRoomPendingMembers() {
        return goalRoomPendingMembers;
    }
}
