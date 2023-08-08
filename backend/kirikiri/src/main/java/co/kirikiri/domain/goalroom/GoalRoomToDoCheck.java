package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomToDoCheck extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_room_member_id", nullable = false)
    private GoalRoomMember goalRoomMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_room_to_do_id", nullable = false)
    private GoalRoomToDo goalRoomToDo;

    public GoalRoomToDoCheck(final GoalRoomMember goalRoomMember, final GoalRoomToDo goalRoomToDo) {
        this.goalRoomMember = goalRoomMember;
        this.goalRoomToDo = goalRoomToDo;
    }

    public GoalRoomToDo getGoalRoomToDo() {
        return goalRoomToDo;
    }
}
