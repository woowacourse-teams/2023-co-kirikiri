package co.kirikiri.todo.domain;

import co.kirikiri.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomToDoCheck extends BaseEntity {

    private Long goalRoomMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_room_to_do_id", nullable = false)
    private GoalRoomToDo goalRoomToDo;

    public GoalRoomToDoCheck(final Long goalRoomMemberId, final GoalRoomToDo goalRoomToDo) {
        this.goalRoomMemberId = goalRoomMemberId;
        this.goalRoomToDo = goalRoomToDo;
    }

    public GoalRoomToDo getGoalRoomToDo() {
        return goalRoomToDo;
    }
}
