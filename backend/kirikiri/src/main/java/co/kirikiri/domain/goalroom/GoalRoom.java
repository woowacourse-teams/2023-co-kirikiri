package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseUpdatedTimeEntity;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_content_id", nullable = false)
    private RoadmapContent roadmapContent;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Embedded
    private final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers();

    @Embedded
    private final GoalRoomMembers goalRoomMembers = new GoalRoomMembers();

    @Embedded
    private final GoalRoomToDos goalRoomToDos = new GoalRoomToDos();

    @Embedded
    private final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes();

    public GoalRoom(final GoalRoomName name, final LimitedMemberCount limitedMemberCount,
                    final RoadmapContent roadmapContent, final Member member) {
        this(null, name, limitedMemberCount, roadmapContent, member);
    }

    public GoalRoom(final Long id, final GoalRoomName name, final LimitedMemberCount limitedMemberCount,
                    final RoadmapContent roadmapContent, final Member member) {
        this.id = id;
        this.name = name;
        this.limitedMemberCount = limitedMemberCount;
        this.roadmapContent = roadmapContent;
        updateLeader(member);
    }

    private void updateLeader(final Member member) {
        final GoalRoomPendingMember leader = new GoalRoomPendingMember(GoalRoomRole.LEADER, member);
        leader.updateGoalRoom(this);
        goalRoomPendingMembers.add(leader);
    }

    public void updateStatus(final GoalRoomStatus status) {
        this.status = status;
    }

    public void join(final Member member) {
        final GoalRoomPendingMember newMember = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, member);
        newMember.updateGoalRoom(this);
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
            throw new BadRequestException("제한 인원이 꽉 찬 골룸에는 참여할 수 없습니다.");
        }
    }

    private void validateStatus() {
        if (status != GoalRoomStatus.RECRUITING) {
            throw new BadRequestException("모집 중이지 않은 골룸에는 참여할 수 없습니다.");
        }
    }

    private void validateAlreadyParticipated(final GoalRoomPendingMember member) {
        if (goalRoomPendingMembers.containGoalRoomPendingMember(member)) {
            throw new BadRequestException("이미 참여한 골룸에는 참여할 수 없습니다.");
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

    public Member findGoalRoomLeader() {
        if (status == GoalRoomStatus.RECRUITING) {
            return goalRoomPendingMembers.findGoalRoomLeader();
        }
        return goalRoomMembers.findGoalRoomLeader();
    }

    public boolean isNotLeader(final Member member) {
        if (status == GoalRoomStatus.RECRUITING) {
            return goalRoomPendingMembers.isNotLeader(member);
        }
        return goalRoomMembers.isNotLeader(member);
    }

    public boolean isCompleted() {
        return this.status == GoalRoomStatus.COMPLETED;
    }

    public GoalRoomToDo findLastGoalRoomTodo() {
        return goalRoomToDos.findLast();
    }

    public Optional<GoalRoomRoadmapNode> getNodeByDate(final LocalDate date) {
        return goalRoomRoadmapNodes.getNodeByDate(date);
    }

    public Integer getCurrentMemberCount() {
        if (status == GoalRoomStatus.RECRUITING || status == GoalRoomStatus.RECRUIT_COMPLETED) {
            return goalRoomPendingMembers.size();
        }
        return goalRoomMembers.size();
    }

    public void addAllGoalRoomMembers(final List<GoalRoomMember> members) {
        this.goalRoomMembers.addAll(new ArrayList<>(members));
    }

    public boolean isGoalRoomMember(final Member member) {
        if (status == GoalRoomStatus.RECRUITING || status == GoalRoomStatus.RECRUIT_COMPLETED) {
            return goalRoomPendingMembers.isMember(member);
        }
        return goalRoomMembers.isMember(member);
    }

    public void leave(final Member member) {
        if (status == GoalRoomStatus.RECRUITING) {
            final GoalRoomPendingMember goalRoomPendingMember = findGoalRoomPendingMemberByMember(member);
            changeRoleIfLeaderLeave(goalRoomPendingMembers, goalRoomPendingMember);
            goalRoomPendingMembers.remove(goalRoomPendingMember);
            return;
        }
        final GoalRoomMember goalRoomMember = findGoalRoomMemberByMember(member);
        changeRoleIfLeaderLeave(goalRoomMembers, goalRoomMember);
        goalRoomMembers.remove(goalRoomMember);
    }

    private GoalRoomPendingMember findGoalRoomPendingMemberByMember(final Member member) {
        return goalRoomPendingMembers.findByMember(member)
                .orElseThrow(() -> new BadRequestException("골룸에 참여한 사용자가 아닙니다. memberId = " + member.getId()));
    }

    private void changeRoleIfLeaderLeave(final GoalRoomPendingMembers goalRoomPendingMembers,
                                         final GoalRoomPendingMember goalRoomPendingMember) {
        if (goalRoomPendingMember.isLeader()) {
            goalRoomPendingMembers.findNextLeader()
                    .ifPresent(GoalRoomPendingMember::becomeLeader);

        }
    }

    private GoalRoomMember findGoalRoomMemberByMember(final Member member) {
        return goalRoomMembers.findByMember(member)
                .orElseThrow(() -> new BadRequestException("골룸에 참여한 사용자가 아닙니다. memberId = " + member.getId()));
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

    public RoadmapContent getRoadmapContent() {
        return roadmapContent;
    }

    public GoalRoomRoadmapNodes getGoalRoomRoadmapNodes() {
        return goalRoomRoadmapNodes;
    }

    public GoalRoomMembers getGoalRoomMembers() {
        return goalRoomMembers;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public GoalRoomToDos getGoalRoomToDos() {
        return goalRoomToDos;
    }
}
