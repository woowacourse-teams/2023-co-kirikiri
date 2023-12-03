package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.member.Member;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomPendingMember extends BaseGoalRoomMember {

    public GoalRoomPendingMember(final GoalRoomRole role, final Member member) {
        super(role, null, null, member);
    }

    public GoalRoomPendingMember(final GoalRoomRole role, final GoalRoom goalRoom, final Member member) {
        super(role, null, goalRoom, member);
    }

    public GoalRoomPendingMember(final GoalRoomRole role, final LocalDateTime joinedAt,
                                 final GoalRoom goalRoom, final Member member) {
        super(role, joinedAt, goalRoom, member);
    }

    public GoalRoomPendingMember(final Long id, final GoalRoomRole role, final LocalDateTime joinedAt,
                                 final GoalRoom goalRoom, final Member member) {
        super(id, role, joinedAt, goalRoom, member);
    }

    public void initGoalRoom(final GoalRoom goalRoom) {
        if (this.goalRoom == null) {
            this.goalRoom = goalRoom;
        }
    }
}
