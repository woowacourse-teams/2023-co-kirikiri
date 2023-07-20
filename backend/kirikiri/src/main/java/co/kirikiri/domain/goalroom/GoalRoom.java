package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseTimeEntity;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoom extends BaseTimeEntity {

    @Enumerated(value = EnumType.STRING)
    private final GoalRoomStatus status = GoalRoomStatus.RECRUITING;
    @Embedded
    private final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(new ArrayList<>());
    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @JoinColumn(name = "goal_room_id", updatable = false)
    private final List<GoalRoomToDo> goalRoomToDos = new ArrayList<>();
    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @JoinColumn(name = "goal_room_id", nullable = false, updatable = false)
    private final List<GoalRoomRoadmapNode> goalRoomRoadmapNodes = new ArrayList<>();
    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true, mappedBy = "goalRoom")
    private final List<GoalRoomMember> goalRoomMembers = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private GoalRoomName name;
    @Embedded
    private LimitedMemberCount limitedMemberCount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_content_id", nullable = false)
    private RoadmapContent roadmapContent;

    public GoalRoom(final GoalRoomName name, final LimitedMemberCount limitedMemberCount, final RoadmapContent roadmapContent) {
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
}
