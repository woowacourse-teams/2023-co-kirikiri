package co.kirikiri.goalroom.domain;

import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomPendingMember extends BaseGoalRoomMember {

    public GoalRoomPendingMember(final GoalRoomRole role, final GoalRoom goalRoom, final Long memberId) {
        this(null, role, null, goalRoom, memberId);
    }

    public GoalRoomPendingMember(final Long id, final GoalRoomRole role, final LocalDateTime joinedAt,
                                 final GoalRoom goalRoom, final Long memberId) {
        super(id, role, joinedAt, goalRoom, memberId);
    }
}
